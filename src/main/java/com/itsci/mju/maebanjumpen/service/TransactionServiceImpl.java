package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.model.Hirer;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.Member;
import com.itsci.mju.maebanjumpen.model.Transaction;
import com.itsci.mju.maebanjumpen.repository.MemberRepository;
import com.itsci.mju.maebanjumpen.repository.TransactionRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, MemberRepository memberRepository) {
        this.transactionRepository = transactionRepository;
        this.memberRepository = memberRepository;
    }

    private void initializeTransactionMemberAndRelated(Transaction transaction) {
        if (transaction != null && transaction.getMember() != null) {
            // Ensure member is initialized before accessing its properties
            Hibernate.initialize(transaction.getMember());
            Member member = transaction.getMember();

            if (member.getPerson() != null) {
                Hibernate.initialize(member.getPerson());
                if (member.getPerson().getLogin() != null) {
                    Hibernate.initialize(member.getPerson().getLogin());
                }
            }

            // You might need to explicitly initialize collections if they are LAZY
            // and you expect them to be serialized for the Transaction response.
            // For example, member.getTransactions() or hirer.getHires().
            // However, for this Transaction service, often only basic member info is needed.
            // If you encounter LazyInitializationException during serialization of the returned Transaction,
            // you might need to add more Hibernate.initialize calls here based on your TransactionSerializer.
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        for (Transaction transaction : transactions) {
            initializeTransactionMemberAndRelated(transaction);
        }
        return transactions;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(int id) {
        // Using @EntityGraph in TransactionRepository's findById should handle member initialization.
        // But adding initializeTransactionMemberAndRelated as a fallback/consistency.
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        transactionOptional.ifPresent(this::initializeTransactionMemberAndRelated);
        return transactionOptional;
    }

    @Override
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        // --- 1. Validate and fetch Member ---
        if (transaction.getMember() == null || transaction.getMember().getId() == null) {
            throw new IllegalArgumentException("Member ID จำเป็นสำหรับการสร้างหรืออัปเดตธุรกรรม.");
        }

        // Fetch the managed Member entity from the database
        // This is crucial. Don't rely on the detached 'member' object from the request for further operations.
        Member existingMember = memberRepository.findById(transaction.getMember().getId())
                .orElseThrow(() -> new RuntimeException("ไม่พบสมาชิกด้วย ID: " + transaction.getMember().getId()));
        transaction.setMember(existingMember); // Attach the managed entity to the transaction

        // --- 2. Set Transaction Date if null ---
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }

        // --- 3. Determine oldStatus for update scenarios ---
        String oldStatus = null;
        if (transaction.getTransactionId() != null) { // Only for existing transactions
            Optional<Transaction> existingOpt = transactionRepository.findById(transaction.getTransactionId());
            if (existingOpt.isPresent()) {
                oldStatus = existingOpt.get().getTransactionStatus();
            }
        }

        // --- 4. Set Initial Transaction Status ---
        if (("Deposit".equalsIgnoreCase(transaction.getTransactionType()) || "เติมเงิน".equalsIgnoreCase(transaction.getTransactionType())) &&
                (transaction.getTransactionStatus() == null || transaction.getTransactionStatus().isEmpty())) {
            transaction.setTransactionStatus("Pending Payment");
        } else if (("Withdrawal".equalsIgnoreCase(transaction.getTransactionType()) || "ถอนเงิน".equalsIgnoreCase(transaction.getTransactionType())) &&
                (transaction.getTransactionStatus() == null || transaction.getTransactionStatus().isEmpty())) {
            transaction.setTransactionStatus("Pending Approve");
        } else if (transaction.getTransactionStatus() == null || transaction.getTransactionStatus().isEmpty()) {
            transaction.setTransactionStatus("Pending");
        }

        // --- 5. Set Transaction Approval Date ---
        String currentTransactionStatus = transaction.getTransactionStatus(); // Use the status just set
        if ("Approved".equalsIgnoreCase(currentTransactionStatus) ||
                "Rejected".equalsIgnoreCase(currentTransactionStatus) ||
                "Completed".equalsIgnoreCase(currentTransactionStatus) ||
                "อนุมัติแล้ว".equalsIgnoreCase(currentTransactionStatus) ||
                "ถูกปฏิเสธ".equalsIgnoreCase(currentTransactionStatus) ||
                "เสร็จสิ้น".equalsIgnoreCase(currentTransactionStatus)) {
            if (transaction.getTransactionApprovalDate() == null) {
                transaction.setTransactionApprovalDate(LocalDateTime.now());
            }
        } else {
            transaction.setTransactionApprovalDate(null);
        }

        // --- 6. Save the Transaction ---
        Transaction savedTransaction = transactionRepository.save(transaction);

        // --- 7. Logic for adjusting Member balance (if status changed to Approved for the first time) ---
        // We use the 'savedTransaction' as it's now managed by JPA/Hibernate and has its ID.
        // Check if the status is now Approved and it wasn't Approved before this save/update.
        if (("Approved".equalsIgnoreCase(savedTransaction.getTransactionStatus()) || "อนุมัติแล้ว".equalsIgnoreCase(savedTransaction.getTransactionStatus())) &&
                !(oldStatus != null && ("Approved".equalsIgnoreCase(oldStatus) || "อนุมัติแล้ว".equalsIgnoreCase(oldStatus)))) {

            Member memberToUpdate = savedTransaction.getMember(); // This 'memberToUpdate' is the already fetched and managed 'existingMember'
            if (memberToUpdate != null) { // Should not be null due to previous checks
                Double currentBalance = memberToUpdate.getBalance();
                Double transactionAmount = savedTransaction.getTransactionAmount();

                if ("Withdrawal".equalsIgnoreCase(savedTransaction.getTransactionType()) || "ถอนเงิน".equalsIgnoreCase(savedTransaction.getTransactionType())) {
                    // --- Handle Withdrawal ---
                    if (currentBalance != null && currentBalance >= transactionAmount) { // Added null check for currentBalance
                        memberToUpdate.setBalance(currentBalance - transactionAmount);
                        memberRepository.save(memberToUpdate); // Persist updated balance
                    } else {
                        // Insufficient funds: Mark transaction as Failed and throw an exception to rollback
                        savedTransaction.setTransactionStatus("Failed");
                        savedTransaction.setTransactionApprovalDate(LocalDateTime.now());
                        transactionRepository.save(savedTransaction); // Save the failed status
                        throw new RuntimeException("ยอดเงินคงเหลือของสมาชิก " + memberToUpdate.getId() + " ไม่พอสำหรับการถอน (" + currentBalance + " < " + transactionAmount + ")");
                    }
                } else if ("Deposit".equalsIgnoreCase(savedTransaction.getTransactionType()) || "เติมเงิน".equalsIgnoreCase(savedTransaction.getTransactionType())) {
                    // --- Handle Deposit ---
                    memberToUpdate.setBalance((currentBalance != null ? currentBalance : 0.0) + transactionAmount); // Handle null currentBalance
                    memberRepository.save(memberToUpdate); // Persist updated balance
                }
                // Add more conditions for other Transaction Types (e.g., service fees, penalties)
            }
        }

        // Initialize related entities for the returned object to prevent LazyInitializationException during serialization
        initializeTransactionMemberAndRelated(savedTransaction);
        return savedTransaction;
    }

    @Override
    @Transactional
    public void deleteTransaction(int id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("ไม่พบธุรกรรมด้วย ID: " + id);
        }
        transactionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByMemberId(int memberId) {
        List<Transaction> transactions = transactionRepository.findByMemberId(memberId);
        for (Transaction transaction : transactions) {
            initializeTransactionMemberAndRelated(transaction);
        }
        return transactions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getWithdrawalRequests() {
        List<Transaction> withdrawalTransactions = transactionRepository
                .findByTransactionTypeOrTransactionType("Withdrawal", "ถอนเงิน");

        for (Transaction transaction : withdrawalTransactions) {
            initializeTransactionMemberAndRelated(transaction);
        }

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

        return withdrawalTransactions;
    }

    @Override
    @Transactional
    public Optional<Transaction> updateWithdrawalRequestStatus(Integer transactionId, String newStatus) {
        return transactionRepository.findById(transactionId).map(existingTransaction -> {
            String oldStatus = existingTransaction.getTransactionStatus(); // เก็บสถานะเดิม

            existingTransaction.setTransactionStatus(newStatus);

            if ("Approved".equalsIgnoreCase(newStatus) || "Rejected".equalsIgnoreCase(newStatus) ||
                    "อนุมัติแล้ว".equalsIgnoreCase(newStatus) || "ถูกปฏิเสธ".equalsIgnoreCase(newStatus) ||
                    "Completed".equalsIgnoreCase(newStatus) || "เสร็จสิ้น".equalsIgnoreCase(newStatus)) {
                existingTransaction.setTransactionApprovalDate(LocalDateTime.now());
            } else {
                existingTransaction.setTransactionApprovalDate(null);
            }

            Transaction savedTransaction = transactionRepository.save(existingTransaction);

            // --- Logic สำหรับหัก/เพิ่มเงินเมื่อสถานะเปลี่ยนเป็น Approved ---
            // ตรวจสอบว่าสถานะเปลี่ยนเป็น Approved และไม่ใช่สถานะเดิม
            if (("Approved".equalsIgnoreCase(savedTransaction.getTransactionStatus()) || "อนุมัติแล้ว".equalsIgnoreCase(savedTransaction.getTransactionStatus())) &&
                    !(oldStatus != null && ("Approved".equalsIgnoreCase(oldStatus) || "อนุมัติแล้ว".equalsIgnoreCase(oldStatus)))) {

                Member memberToUpdate = savedTransaction.getMember(); // This is the managed entity
                if (memberToUpdate != null) {
                    Double currentBalance = memberToUpdate.getBalance();
                    Double transactionAmount = savedTransaction.getTransactionAmount();

                    if ("Withdrawal".equalsIgnoreCase(savedTransaction.getTransactionType()) || "ถอนเงิน".equalsIgnoreCase(savedTransaction.getTransactionType())) {
                        // หักเงินสำหรับ Withdrawal
                        if (currentBalance != null && currentBalance >= transactionAmount) { // Added null check for currentBalance
                            memberToUpdate.setBalance(currentBalance - transactionAmount);
                            memberRepository.save(memberToUpdate); // บันทึก Member ที่อัปเดต balance
                        } else {
                            // กรณีเงินไม่พอ: เปลี่ยนสถานะธุรกรรมเป็น "Failed" และ throw Exception เพื่อ rollback
                            savedTransaction.setTransactionStatus("Failed");
                            savedTransaction.setTransactionApprovalDate(LocalDateTime.now());
                            transactionRepository.save(savedTransaction); // Save the failed status
                            throw new RuntimeException("ยอดเงินคงเหลือของสมาชิก " + memberToUpdate.getId() + " ไม่พอสำหรับการถอน (" + (currentBalance != null ? currentBalance : 0.0) + " < " + transactionAmount + ")");
                        }
                    } else if ("Deposit".equalsIgnoreCase(savedTransaction.getTransactionType()) || "เติมเงิน".equalsIgnoreCase(savedTransaction.getTransactionType())) {
                        // เพิ่มเงินสำหรับ Deposit
                        memberToUpdate.setBalance((currentBalance != null ? currentBalance : 0.0) + transactionAmount); // Handle null currentBalance
                        memberRepository.save(memberToUpdate); // บันทึก Member ที่อัปเดต balance
                    }
                }
            }
            // Initialize related entities for the returned object
            initializeTransactionMemberAndRelated(savedTransaction);
            return savedTransaction;
        });
    }

    @Override
    public String getLocalizedStatus(String status, boolean isEnglish) {
        if (status == null) {
            return isEnglish ? "Unknown" : "ไม่ทราบสถานะ";
        }
        if (isEnglish) {
            return status;
        } else {
            switch (status.toLowerCase()) {
                case "pending approve":
                    return "กำลังรอตรวจสอบ";
                case "pending payment":
                    return "รอชำระเงิน";
                case "approved":
                    return "อนุมัติแล้ว";
                case "rejected":
                    return "ถูกปฏิเสธ";
                case "completed":
                    return "เสร็จสิ้น";
                case "failed":
                    return "ล้มเหลว";
                default:
                    return "ไม่ทราบสถานะ";
            }
        }
    }

    @Override
    public String getStatusColorHex(String status) {
        if (status == null) return "#808080";

        switch (status.toLowerCase()) {
            case "pending approve":
            case "กำลังรอตรวจสอบ":
            case "pending payment":
            case "รอชำระเงิน":
                return "#FFA500";
            case "approved":
            case "อนุมัติแล้ว":
            case "completed":
            case "เสร็จสิ้น":
                return "#008000";
            case "rejected":
            case "ถูกปฏิเสd":
            case "failed":
            case "ล้มเหลว":
                return "#FF0000";
            default:
                return "#808080";
        }
    }
}