package com.itsci.mju.maebanjumpen.transaction.service.impl

import com.fasterxml.jackson.databind.JsonNode
import com.itsci.mju.maebanjumpen.entity.Transaction
import com.itsci.mju.maebanjumpen.mapper.TransactionMapper
import com.itsci.mju.maebanjumpen.partyrole.repository.MemberRepository
import com.itsci.mju.maebanjumpen.transaction.dto.TransactionDTO
import com.itsci.mju.maebanjumpen.transaction.repository.TransactionRepository
import com.itsci.mju.maebanjumpen.transaction.service.TransactionService
import org.hibernate.Hibernate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.Optional

@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val memberRepository: MemberRepository,
    private val transactionMapper: TransactionMapper
) : TransactionService {

    private fun initializeTransactionMemberAndRelated(transaction: Transaction?) {
        if (transaction?.member != null) {
            Hibernate.initialize(transaction.member)
            val member = transaction.member!!

            member.person?.let { person ->
                Hibernate.initialize(person)
                person.login?.let { Hibernate.initialize(it) }
            }
        }
    }

    @Transactional(readOnly = true)
    override fun getAllTransactions(): List<TransactionDTO> {
        val transactions = transactionRepository.findAll()
        transactions.forEach { initializeTransactionMemberAndRelated(it) }
        return transactionMapper.toDtoList(transactions)
    }

    @Transactional(readOnly = true)
    override fun getTransactionById(id: Int): Optional<TransactionDTO> {
        val transactionOptional = transactionRepository.findById(id)
        transactionOptional.ifPresent { initializeTransactionMemberAndRelated(it) }
        return transactionOptional.map { transactionMapper.toDto(it) }
    }

    @Transactional
    override fun saveTransaction(transactionDto: TransactionDTO): TransactionDTO {
        if (transactionDto.member?.id == null) {
            throw IllegalArgumentException("Member ID is required for transaction. Please provide member object with ID.")
        }

        val memberId = transactionDto.member!!.id!!

        val existingMember = memberRepository.findById(memberId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with ID: $memberId") }

        val transaction = transactionMapper.toEntity(transactionDto)
        transaction.member = existingMember

        if (transaction.transactionDate == null) {
            transaction.transactionDate = LocalDateTime.now()
        }

        if ("Withdrawal".equals(transaction.transactionType, ignoreCase = true)) {
            val hasPrompay = !transaction.prompayNumber.isNullOrBlank()
            val hasBankDetails = !transaction.bankAccountNumber.isNullOrBlank() && !transaction.bankAccountName.isNullOrBlank()

            if (!hasPrompay && !hasBankDetails) {
                throw IllegalArgumentException("Withdrawal transaction requires either Prompay number or complete Bank Account details (number and name).")
            }
        }

        var oldStatus: String? = null
        transaction.transactionId?.let { id ->
            transactionRepository.findById(id).ifPresent { oldStatus = it.transactionStatus }
        }

        if (transaction.transactionStatus.isNullOrEmpty()) {
            transaction.transactionStatus = when {
                "Deposit".equals(transaction.transactionType, ignoreCase = true) -> "Pending Payment"
                "Withdrawal".equals(transaction.transactionType, ignoreCase = true) -> "Pending Approve"
                else -> "Pending"
            }
        }

        val currentTransactionStatus = transaction.transactionStatus
        if (listOf("Approved", "Rejected", "Completed", "SUCCESS").any { it.equals(currentTransactionStatus, ignoreCase = true) }) {
            if (transaction.transactionApprovalDate == null) {
                transaction.transactionApprovalDate = LocalDateTime.now()
            }
        } else {
            transaction.transactionApprovalDate = null
        }

        val savedTransaction = transactionRepository.save(transaction)
        val savedStatusEnglish = savedTransaction.transactionStatus?.uppercase() ?: ""

        if ((savedStatusEnglish == "APPROVED" || savedStatusEnglish == "SUCCESS") &&
            !(oldStatus?.uppercase() == "APPROVED" || oldStatus?.uppercase() == "SUCCESS")) {

            val memberToUpdate = savedTransaction.member
            if (memberToUpdate != null) {
                val currentBalance = memberToUpdate.balance ?: 0.0
                val transactionAmount = savedTransaction.transactionAmount ?: 0.0

                if ("Withdrawal".equals(savedTransaction.transactionType, ignoreCase = true)) {
                    if (currentBalance >= transactionAmount) {
                        memberToUpdate.balance = currentBalance - transactionAmount
                        memberRepository.save(memberToUpdate)
                    } else {
                        savedTransaction.transactionStatus = "Failed"
                        savedTransaction.transactionApprovalDate = LocalDateTime.now()
                        transactionRepository.save(savedTransaction)
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds for withdrawal ($currentBalance < $transactionAmount)")
                    }
                } else if ("Deposit".equals(savedTransaction.transactionType, ignoreCase = true)) {
                    memberToUpdate.balance = currentBalance + transactionAmount
                    memberRepository.save(memberToUpdate)
                }
            }
        }

        initializeTransactionMemberAndRelated(savedTransaction)
        return transactionMapper.toDto(savedTransaction)
    }

    @Transactional
    override fun deleteTransaction(id: Int) {
        if (!transactionRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with ID: $id")
        }
        transactionRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun getTransactionsByMemberId(memberId: Int): List<TransactionDTO> {
        val transactions = transactionRepository.findByMemberId(memberId)
        transactions.forEach { initializeTransactionMemberAndRelated(it) }
        return transactionMapper.toDtoList(transactions)
    }

    @Transactional(readOnly = true)
    override fun getWithdrawalRequests(): List<TransactionDTO> {
        val withdrawalTransactions = transactionRepository.findByTransactionTypeOrTransactionType("Withdrawal", "ถอนเงิน")
        withdrawalTransactions.forEach { initializeTransactionMemberAndRelated(it) }

        val sortedTransactions = withdrawalTransactions.sortedWith(compareBy<Transaction> { transaction ->
            val status = transaction.transactionStatus?.lowercase() ?: ""
            val isPending = status == "pending approve" || status == "กำลังรอตรวจสอบ"
            if (isPending) 0 else 1
        }.thenByDescending { it.transactionDate ?: LocalDateTime.MIN })

        return transactionMapper.toDtoList(sortedTransactions)
    }

    @Transactional
    override fun updateWithdrawalRequestStatus(transactionId: Int, newStatus: String): Optional<TransactionDTO> {
        return transactionRepository.findById(transactionId).map { existingTransaction ->
            val oldStatus = existingTransaction.transactionStatus

            existingTransaction.transactionStatus = newStatus

            if (listOf("Approved", "Rejected", "Completed", "SUCCESS").any { it.equals(newStatus, ignoreCase = true) }) {
                existingTransaction.transactionApprovalDate = LocalDateTime.now()
            } else {
                existingTransaction.transactionApprovalDate = null
            }

            val savedTransaction = transactionRepository.save(existingTransaction)
            val savedStatusEnglish = savedTransaction.transactionStatus?.uppercase() ?: ""

            if ((savedStatusEnglish == "APPROVED" || savedStatusEnglish == "SUCCESS") &&
                !(oldStatus?.uppercase() == "APPROVED" || oldStatus?.uppercase() == "SUCCESS")) {

                val memberToUpdate = savedTransaction.member
                if (memberToUpdate != null) {
                    val currentBalance = memberToUpdate.balance ?: 0.0
                    val transactionAmount = savedTransaction.transactionAmount ?: 0.0

                    if ("Withdrawal".equals(savedTransaction.transactionType, ignoreCase = true)) {
                        if (currentBalance >= transactionAmount) {
                            memberToUpdate.balance = currentBalance - transactionAmount
                            memberRepository.save(memberToUpdate)
                        } else {
                            savedTransaction.transactionStatus = "Failed"
                            savedTransaction.transactionApprovalDate = LocalDateTime.now()
                            transactionRepository.save(savedTransaction)
                            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds for withdrawal ($currentBalance < $transactionAmount)")
                        }
                    } else if ("Deposit".equals(savedTransaction.transactionType, ignoreCase = true)) {
                        memberToUpdate.balance = currentBalance + transactionAmount
                        memberRepository.save(memberToUpdate)
                    }
                }
            }
            initializeTransactionMemberAndRelated(savedTransaction)
            transactionMapper.toDto(savedTransaction)
        }
    }

    @Transactional
    override fun processOmiseChargeComplete(root: JsonNode) {
        val chargeStatus = root.path("data").path("status").asText()
        val paid = root.path("data").path("paid").asBoolean()
        val amountInSatang = root.path("data").path("amount").asDouble()
        val ourTransactionId = root.path("data").path("metadata").path("transaction_id").asText()

        if (ourTransactionId.isNullOrEmpty()) {
            throw IllegalArgumentException("Metadata 'transaction_id' is missing from Omise payload.")
        }

        val optionalTransaction = transactionRepository.findById(ourTransactionId.toInt())

        if (optionalTransaction.isPresent) {
            val transaction = optionalTransaction.get()
            val oldStatus = transaction.transactionStatus

            if (transaction.member?.id == null) {
                System.err.println("Error: Member not found or invalid ID for transaction ID: $ourTransactionId")
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Member associated with transaction not found.")
            }

            when {
                chargeStatus == "successful" && paid -> {
                    transaction.transactionStatus = "SUCCESS"
                    transaction.transactionApprovalDate = LocalDateTime.now()

                    val member = transaction.member
                    if (member != null) {
                        if (!listOf("SUCCESS", "APPROVED").any { it.equals(oldStatus, ignoreCase = true) }) {
                            val amountInBaht = amountInSatang / 100.0
                            val currentMemberBalance = member.balance ?: 0.0
                            member.balance = currentMemberBalance + amountInBaht
                            memberRepository.save(member)
                            println("Member ID: ${member.id} balance updated to: ${member.balance}")
                        } else {
                            println("Transaction ID: $ourTransactionId already processed/approved. Skipping balance update.")
                        }
                    }
                }
                chargeStatus == "failed" -> {
                    transaction.transactionStatus = "FAILED"
                    println("Transaction ID: $ourTransactionId failed.")
                }
                else -> {
                    transaction.transactionStatus = chargeStatus.uppercase()
                    println("Transaction ID: $ourTransactionId status: $chargeStatus")
                }
            }

            transactionRepository.save(transaction)
        } else {
            System.err.println("Transaction with ID $ourTransactionId not found in our system.")
        }
    }

    override fun getLocalizedStatus(status: String?, isEnglish: Boolean): String {
        if (status == null) {
            return if (isEnglish) "Unknown" else "ไม่ทราบสถานะ"
        }
        return if (isEnglish) {
            status
        } else {
            when (status.uppercase()) {
                "PENDING APPROVE", "PENDING" -> "กำลังรอตรวจสอบ"
                "PENDING PAYMENT", "QR GENERATED" -> "รอชำระเงิน"
                "APPROVED" -> "อนุมัติแล้ว"
                "REJECTED" -> "ถูกปฏิเสธ"
                "COMPLETED" -> "เสร็จสิ้น"
                "FAILED" -> "ล้มเหลว"
                "SUCCESS" -> "สำเร็จ"
                else -> "ไม่ทราบสถานะ"
            }
        }
    }

    override fun getStatusColorHex(status: String?): String {
        if (status == null) return "#808080"

        return when (status.uppercase()) {
            "PENDING APPROVE", "PENDING PAYMENT", "QR GENERATED" -> "#FFA500"
            "APPROVED", "COMPLETED", "SUCCESS" -> "#008000"
            "REJECTED", "FAILED" -> "#FF0000"
            else -> "#808080"
        }
    }
}

