package com.itsci.mju.maebanjumpen.transaction.dto

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import java.time.LocalDateTime

data class TransactionDTO(
    var id: Long? = 0,
    var transactionType: String? = null,
    var transactionAmount: Double? = null,
    var transactionDate: LocalDateTime? = null,
    var transactionStatus: String? = null,
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    var member: MemberDTO? = null,
    var prompayNumber: String? = null,
    var bankAccountNumber: String? = null,
    var bankAccountName: String? = null,
    var transactionApprovalDate: LocalDateTime? = null
)

