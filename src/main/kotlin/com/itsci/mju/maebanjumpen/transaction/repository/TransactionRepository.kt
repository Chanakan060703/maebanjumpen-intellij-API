package com.itsci.mju.maebanjumpen.transaction.repository

import com.itsci.mju.maebanjumpen.entity.Transaction
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {

    fun findByTransactionTypeAndTransactionStatus(transactionType: String, transactionStatus: String): List<Transaction>

    fun findByMemberId(memberId: Int): List<Transaction>

    @EntityGraph(attributePaths = ["member", "member.person", "member.person.login"])
    override fun findById(id: Long): Optional<Transaction>

    fun findByTransactionType(transactionType: String): List<Transaction>

    fun findByTransactionTypeOrTransactionType(type1: String, type2: String): List<Transaction>
}

