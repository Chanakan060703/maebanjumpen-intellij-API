package com.itsci.mju.maebanjumpen.transaction.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.itsci.mju.maebanjumpen.transaction.dto.TransactionDTO;
import com.itsci.mju.maebanjumpen.mapper.TransactionMapper;
import com.itsci.mju.maebanjumpen.entity.Member;
import com.itsci.mju.maebanjumpen.entity.Transaction;
import com.itsci.mju.maebanjumpen.partyrole.repository.MemberRepository;
import com.itsci.mju.maebanjumpen.transaction.repository.TransactionRepository;
import com.itsci.mju.maebanjumpen.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;
    private final TransactionMapper transactionMapper;

    private void initializeTransactionMemberAndRelated(Transaction transaction) {
        if (transaction != null && transaction.getMember() != null) {
            Hibernate.initialize(transaction.getMember());
            Member member = transaction.getMember();

            if (member.getPerson() != null) {
                Hibernate.initialize(member.getPerson());
                if (member.getPerson().getLogin() != null) {
                    Hibernate.initialize(member.getPerson().getLogin());
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        for (Transaction transaction : transactions) {
            initializeTransactionMemberAndRelated(transaction);
        }
        return transactionMapper.toDtoList(transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionDTO> getTransactionById(int id) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);

        transactionOptional.ifPresent(this::initializeTransactionMemberAndRelated);

        return transactionOptional.map(transactionMapper::toDto);
    }

    @Override
    @Transactional
    public TransactionDTO saveTransaction(TransactionDTO transactionDto) {

        // 1. Validate and fetch Member ID from DTO
        // 🚨 FIX 1: ตรวจสอบ Member object และใช้ getMember().getId() แทน getMemberId()
        if (transactionDto.getMember() == null || transactionDto.getMember().getId() == null) {
            throw new IllegalArgumentException("Member ID is required for transaction. Please provide member object with ID.");
        }

        // ดึง memberId ออกมาเก็บไว้เพื่อใช้ต่อ
        Integer memberId = transactionDto.getMember().getId();

        // 2. Fetch the managed Member entity
        // 🚨 FIX 2: ใช้ memberId ที่ดึงจาก nested object
        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with ID: " + memberId));

        // 3. Convert DTO to Entity and attach managed Member
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setMember(existingMember);

        // 4. Set Transaction Date if null
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }

        // 4.5. 🎯 ADD WITHDRAWAL VALIDATION LOGIC HERE (ปรับปรุงแล้ว)
        // ตรวจสอบว่าถ้าเป็นรายการถอนเงิน ต้องมีรายละเอียดปลายทาง
        if ("Withdrawal".equalsIgnoreCase(transaction.getTransactionType())) {
            // A. ตรวจสอบ Prompay Number (ต้องมีค่าและไม่ว่างเปล่า)
            boolean hasPrompay = transaction.getPrompayNumber() != null && !transaction.getPrompayNumber().trim().isEmpty();

            // B. ตรวจสอบรายละเอียดบัญชีธนาคาร (ต้องมีทั้งเลขที่และชื่อบัญชี และไม่ว่างเปล่า)
            boolean hasBankDetails = (transaction.getBankAccountNumber() != null && !transaction.getBankAccountNumber().trim().isEmpty()) &&
                    (transaction.getBankAccountName() != null && !transaction.getBankAccountName().trim().isEmpty());

            // ต้องมีอย่างน้อยหนึ่งช่องทางที่สมบูรณ์ (Prompay หรือ Bank Details)
            if (!hasPrompay && !hasBankDetails) {
                // Throwing IllegalArgumentException will result in 400 BAD_REQUEST in the Controller
                throw new IllegalArgumentException("Withdrawal transaction requires either Prompay number or complete Bank Account details (number and name).");
            }
        }
        // 🎯 END OF WITHDRAWAL VALIDATION

        // 5. Determine oldStatus for update scenarios
        String oldStatus = null;
        if (transaction.getTransactionId() != null) {
            Optional<Transaction> existingOpt = transactionRepository.findById(transaction.getTransactionId());
            if (existingOpt.isPresent()) {
                oldStatus = existingOpt.get().getTransactionStatus();
            }
        }

        // 6. Set Initial Transaction Status (ใช้ภาษาอังกฤษเป็นหลัก)
        if (transaction.getTransactionStatus() == null || transaction.getTransactionStatus().isEmpty()) {
            if ("Deposit".equalsIgnoreCase(transaction.getTransactionType())) {
                transaction.setTransactionStatus("Pending Payment");
            } else if ("Withdrawal".equalsIgnoreCase(transaction.getTransactionType())) {
                transaction.setTransactionStatus("Pending Approve");
            } else {
                transaction.setTransactionStatus("Pending");
            }
        }

        // 7. Set Transaction Approval Date (ใช้ภาษาอังกฤษเป็นหลัก)
        String currentTransactionStatus = transaction.getTransactionStatus();
        if ("Approved".equalsIgnoreCase(currentTransactionStatus) ||
                "Rejected".equalsIgnoreCase(currentTransactionStatus) ||
                "Completed".equalsIgnoreCase(currentTransactionStatus) ||
                "SUCCESS".equalsIgnoreCase(currentTransactionStatus)) {
            if (transaction.getTransactionApprovalDate() == null) {
                transaction.setTransactionApprovalDate(LocalDateTime.now());
            }
        } else {
            transaction.setTransactionApprovalDate(null);
        }

        // 8. Save the Transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        String savedStatusEnglish = savedTransaction.getTransactionStatus().toUpperCase();

        // 9. Logic for adjusting Member balance (Run only if status is Approved/SUCCESS and wasn't before)
        if ((savedStatusEnglish.equals("APPROVED") || savedStatusEnglish.equals("SUCCESS")) &&
                !(oldStatus != null && (oldStatus.toUpperCase().equals("APPROVED") || oldStatus.toUpperCase().equals("SUCCESS")))) {

            Member memberToUpdate = savedTransaction.getMember();
            if (memberToUpdate != null) {
                Double currentBalance = memberToUpdate.getBalance();
                Double transactionAmount = savedTransaction.getTransactionAmount();

                if ("Withdrawal".equalsIgnoreCase(savedTransaction.getTransactionType())) {
                    if (currentBalance != null && currentBalance >= transactionAmount) {
                        memberToUpdate.setBalance(currentBalance - transactionAmount);
                        memberRepository.save(memberToUpdate);
                    } else {
                        // Insufficient funds: Mark transaction as Failed and throw an exception to rollback
                        savedTransaction.setTransactionStatus("Failed");
                        savedTransaction.setTransactionApprovalDate(LocalDateTime.now());
                        transactionRepository.save(savedTransaction);
                        // Using ResponseStatusException here will be caught by the @Transactional block and rollback
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds for withdrawal (" + (currentBalance != null ? currentBalance : 0.0) + " < " + transactionAmount + ")");
                    }
                } else if ("Deposit".equalsIgnoreCase(savedTransaction.getTransactionType())) {
                    memberToUpdate.setBalance((currentBalance != null ? currentBalance : 0.0) + transactionAmount);
                    memberRepository.save(memberToUpdate);
                }
            }
        }

        initializeTransactionMemberAndRelated(savedTransaction);
        return transactionMapper.toDto(savedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(int id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByMemberId(int memberId) {
        List<Transaction> transactions = transactionRepository.findByMemberId(memberId);
        for (Transaction transaction : transactions) {
            initializeTransactionMemberAndRelated(transaction);
        }
        return transactionMapper.toDtoList(transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getWithdrawalRequests() {
        List<Transaction> withdrawalTransactions = transactionRepository
                .findByTransactionTypeOrTransactionType("Withdrawal", "ถอนเงิน");

        for (Transaction transaction : withdrawalTransactions) {
            initializeTransactionMemberAndRelated(transaction);
        }

        // Sorting logic (Pending first, then by date desc)
        withdrawalTransactions.sort((a, b) -> {
            String statusA = (a.getTransactionStatus() != null) ? a.getTransactionStatus().toLowerCase() : "";
            String statusB = (b.getTransactionStatus() != null) ? b.getTransactionStatus().toLowerCase() : "";

            boolean isPendingA = statusA.equals("pending approve") || statusA.equals("กำลังรอตรวจสอบ");
            boolean isPendingB = statusB.equals("pending approve") || statusB.equals("กำลังรอตรวจสอบ");

            if (isPendingA && !isPendingB) {
                return -1;
            }
            if (!isPendingA && isPendingB) {
                return 1;
            }

            LocalDateTime dateA = (a.getTransactionDate() != null) ? a.getTransactionDate() : LocalDateTime.MIN;
            LocalDateTime dateB = (b.getTransactionDate() != null) ? b.getTransactionDate() : LocalDateTime.MIN;
            return dateB.compareTo(dateA);
        });

        return transactionMapper.toDtoList(withdrawalTransactions);
    }


    @Override
    @Transactional
    public Optional<TransactionDTO> updateWithdrawalRequestStatus(Integer transactionId, String newStatus) {
        return transactionRepository.findById(transactionId).map(existingTransaction -> {
            String oldStatus = existingTransaction.getTransactionStatus();

            existingTransaction.setTransactionStatus(newStatus);

            if ("Approved".equalsIgnoreCase(newStatus) || "Rejected".equalsIgnoreCase(newStatus) ||
                    "Completed".equalsIgnoreCase(newStatus) || "SUCCESS".equalsIgnoreCase(newStatus)) {
                existingTransaction.setTransactionApprovalDate(LocalDateTime.now());
            } else {
                existingTransaction.setTransactionApprovalDate(null);
            }

            Transaction savedTransaction = transactionRepository.save(existingTransaction);
            String savedStatusEnglish = savedTransaction.getTransactionStatus().toUpperCase();

            // Logic for adjusting Member balance
            if ((savedStatusEnglish.equals("APPROVED") || savedStatusEnglish.equals("SUCCESS")) &&
                    !(oldStatus != null && (oldStatus.toUpperCase().equals("APPROVED") || oldStatus.toUpperCase().equals("SUCCESS")))) {

                Member memberToUpdate = savedTransaction.getMember();
                if (memberToUpdate != null) {
                    Double currentBalance = memberToUpdate.getBalance();
                    Double transactionAmount = savedTransaction.getTransactionAmount();

                    if ("Withdrawal".equalsIgnoreCase(savedTransaction.getTransactionType())) {
                        if (currentBalance != null && currentBalance >= transactionAmount) {
                            memberToUpdate.setBalance(currentBalance - transactionAmount);
                            memberRepository.save(memberToUpdate);
                        } else {
                            // Insufficient funds
                            savedTransaction.setTransactionStatus("Failed");
                            savedTransaction.setTransactionApprovalDate(LocalDateTime.now());
                            transactionRepository.save(savedTransaction);
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds for withdrawal (" + (currentBalance != null ? currentBalance : 0.0) + " < " + transactionAmount + ")");
                        }
                    } else if ("Deposit".equalsIgnoreCase(savedTransaction.getTransactionType())) {
                        memberToUpdate.setBalance((currentBalance != null ? currentBalance : 0.0) + transactionAmount);
                        memberRepository.save(memberToUpdate);
                    }
                }
            }
            initializeTransactionMemberAndRelated(savedTransaction);
            return transactionMapper.toDto(savedTransaction);
        });
    }

    @Override
    @Transactional
    public void processOmiseChargeComplete(JsonNode root) throws Exception {
        String chargeStatus = root.path("data").path("status").asText();
        boolean paid = root.path("data").path("paid").asBoolean();
        double amountInSatang = root.path("data").path("amount").asDouble();
        String ourTransactionId = root.path("data").path("metadata").path("transaction_id").asText();

        if (ourTransactionId == null || ourTransactionId.isEmpty()) {
            throw new IllegalArgumentException("Metadata 'transaction_id' is missing from Omise payload.");
        }

        Optional<Transaction> optionalTransaction = transactionRepository.findById(Integer.parseInt(ourTransactionId));

        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            String oldStatus = transaction.getTransactionStatus(); // เก็บสถานะเดิม

            if (transaction.getMember() == null || transaction.getMember().getId() == null) {
                System.err.println("Error: Member not found or invalid ID for transaction ID: " + ourTransactionId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member associated with transaction not found.");
            }

            if ("successful".equals(chargeStatus) && paid) {
                transaction.setTransactionStatus("SUCCESS");
                transaction.setTransactionApprovalDate(LocalDateTime.now());

                Member member = transaction.getMember();
                if (member != null) {
                    // Logic: อัปเดตยอดเงินเฉพาะถ้าสถานะเดิมไม่ใช่ SUCCESS/Approved (Idempotency)
                    if (!("SUCCESS".equalsIgnoreCase(oldStatus) || "APPROVED".equalsIgnoreCase(oldStatus))) {
                        double amountInBaht = amountInSatang / 100.0;
                        double currentMemberBalance = member.getBalance() != null ? member.getBalance() : 0.0;
                        member.setBalance(currentMemberBalance + amountInBaht);
                        memberRepository.save(member);
                        System.out.println("Member ID: " + member.getId() + " balance updated to: " + member.getBalance());
                    } else {
                        System.out.println("Transaction ID: " + ourTransactionId + " already processed/approved. Skipping balance update.");
                    }
                } else {
                    // This block is technically redundant due to the check above, but kept for safety
                    System.err.println("Error: Member not found for transaction ID: " + ourTransactionId);
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member associated with transaction not found.");
                }

            } else if ("failed".equals(chargeStatus)) {
                transaction.setTransactionStatus("FAILED");
                System.out.println("Transaction ID: " + ourTransactionId + " failed.");
            } else {
                // สำหรับสถานะอื่นๆ ที่ Omise อาจส่งมา
                transaction.setTransactionStatus(chargeStatus.toUpperCase());
                System.out.println("Transaction ID: " + ourTransactionId + " status: " + chargeStatus);
            }

            transactionRepository.save(transaction);

        } else {
            System.err.println("Transaction with ID " + ourTransactionId + " not found in our system.");
            // ไม่ Throw Exception 404 เพื่อให้ Webhook คืน 200 OK
        }
    }


    @Override
    public String getLocalizedStatus(String status, boolean isEnglish) {
        if (status == null) {
            return isEnglish ? "Unknown" : "ไม่ทราบสถานะ";
        }
        if (isEnglish) {
            return status;
        } else {
            switch (status.toUpperCase()) {
                case "PENDING APPROVE":
                case "PENDING":
                    return "กำลังรอตรวจสอบ";
                case "PENDING PAYMENT":
                case "QR GENERATED": // เพิ่มสถานะนี้
                    return "รอชำระเงิน";
                case "APPROVED":
                    return "อนุมัติแล้ว";
                case "REJECTED":
                    return "ถูกปฏิเสธ";
                case "COMPLETED":
                    return "เสร็จสิ้น";
                case "FAILED":
                    return "ล้มเหลว";
                case "SUCCESS":
                    return "สำเร็จ";
                default:
                    return "ไม่ทราบสถานะ";
            }
        }
    }

    @Override
    public String getStatusColorHex(String status) {
        if (status == null) return "#808080";

        switch (status.toUpperCase()) {
            case "PENDING APPROVE":
            case "PENDING PAYMENT":
            case "QR GENERATED": // เพิ่มสถานะนี้
                return "#FFA500";
            case "APPROVED":
            case "COMPLETED":
            case "SUCCESS":
                return "#008000";
            case "REJECTED":
            case "FAILED":
                return "#FF0000";
            default:
                return "#808080";
        }
    }
}