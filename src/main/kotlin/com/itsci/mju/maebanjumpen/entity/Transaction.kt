package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "transaction_type", nullable = false, length = 255)
    var transactionType: String = "",

    @Column(nullable = false)
    var transactionAmount: Double = 0.0,

    @Column(name = "transaction_date", nullable = false)
    var transactionDate: LocalDateTime? = null,

    @Column(name = "transaction_status", nullable = false, length = 255)
    var transactionStatus: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member? = null,

    @Column(name = "prompay_number", length = 50)
    var prompayNumber: String? = null,

    @Column(name = "bank_account_number", length = 50)
    var bankAccountNumber: String? = null,

    @Column(name = "bank_account_name", length = 255)
    var bankAccountName: String? = null,

    @Column(name = "transaction_approval_date")
    var transactionApprovalDate: LocalDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Transaction) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Transaction(transactionId=$id, transactionType='$transactionType', transactionAmount=$transactionAmount)"
}

