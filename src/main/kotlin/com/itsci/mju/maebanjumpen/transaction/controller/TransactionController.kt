package com.itsci.mju.maebanjumpen.transaction.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import com.itsci.mju.maebanjumpen.partyrole.service.MemberService
import com.itsci.mju.maebanjumpen.transaction.dto.QrCodeRequestDTO
import com.itsci.mju.maebanjumpen.transaction.dto.TransactionDTO
import com.itsci.mju.maebanjumpen.transaction.service.OmiseService
import com.itsci.mju.maebanjumpen.transaction.service.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/maeban")
class TransactionController(
    private val transactionService: TransactionService,
    private val memberService: MemberService,
    private val omiseService: OmiseService,
    private val objectMapper: ObjectMapper
) {

    @GetMapping("/transactions")
    fun getAllTransactions(): ResponseEntity<List<TransactionDTO>> {
        val transactions = transactionService.getAllTransactions()
        return ResponseEntity.ok(transactions)
    }

    @GetMapping("/transactions/{id}")
    fun getTransactionById(@PathVariable id: Int): ResponseEntity<TransactionDTO> {
        val transaction = transactionService.getTransactionById(id)
        return transaction.map { ResponseEntity.ok(it) }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    @GetMapping(value = ["/transactions"], params = ["memberId"])
    fun getTransactionsByMemberId(@RequestParam memberId: Int): ResponseEntity<List<TransactionDTO>> {
        val transactions = transactionService.getTransactionsByMemberId(memberId)
        return if (transactions.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(transactions)
        }
    }

    @GetMapping("/transactions/{transactionId}/status")
    fun getTransactionStatus(@PathVariable transactionId: Int): ResponseEntity<Map<String, String>> {
        val optionalTransaction = transactionService.getTransactionById(transactionId)

        return if (optionalTransaction.isPresent) {
            val transaction = optionalTransaction.get()
            val response = mapOf(
                "transactionId" to transaction.transactionId.toString(),
                "transactionStatus" to (transaction.transactionStatus ?: "")
            )
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Transaction not found"))
        }
    }

    @PostMapping("/transactions")
    fun createTransaction(@RequestBody transactionDto: TransactionDTO): ResponseEntity<TransactionDTO?> {
        return try {
            if (transactionDto.member?.id == null) {
                throw IllegalArgumentException("Member ID is missing or invalid in the request.")
            }

            val savedTransaction = transactionService.saveTransaction(transactionDto)
            ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(null)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().body(null)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create transaction: ${e.message}", e)
        }
    }

    @PatchMapping("/transactions/{transactionId}/status")
    fun updateTransactionStatus(
        @PathVariable transactionId: Int,
        @RequestBody requestBody: Map<String, String>
    ): ResponseEntity<Map<String, String>> {
        return try {
            val newStatus = requestBody["newStatus"]

            if (newStatus.isNullOrEmpty()) {
                return ResponseEntity.badRequest().body(mapOf("error" to "New status is required."))
            }

            val updatedTransaction = transactionService.updateWithdrawalRequestStatus(transactionId, newStatus)

            if (updatedTransaction.isEmpty) {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Transaction not found."))
            } else {
                ResponseEntity.ok(mapOf(
                    "message" to "Transaction status updated successfully.",
                    "transactionId" to updatedTransaction.get().transactionId.toString(),
                    "newStatus" to (updatedTransaction.get().transactionStatus ?: "")
                ))
            }
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (e.message ?: "Unknown error")))
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update transaction status: ${e.message}", e)
        }
    }

    @PutMapping("/transactions/{id}")
    fun updateTransaction(@PathVariable id: Int, @RequestBody transactionDto: TransactionDTO): ResponseEntity<TransactionDTO?> {
        if (transactionService.getTransactionById(id).isEmpty) {
            return ResponseEntity.notFound().build()
        }
        transactionDto.transactionId = id
        return try {
            val updatedTransaction = transactionService.saveTransaction(transactionDto)
            ResponseEntity.ok(updatedTransaction)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().body(null)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update transaction: ${e.message}", e)
        }
    }

    @DeleteMapping("/transactions/{id}")
    fun deleteTransaction(@PathVariable id: Int): ResponseEntity<Void> {
        return try {
            transactionService.deleteTransaction(id)
            ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/transactions/qrcode/deposit")
    fun createDepositQrCode(@RequestBody request: QrCodeRequestDTO): ResponseEntity<Map<String, Any>> {
        val memberId = request.memberId
        val amount = request.amount

        if (memberId == null) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Member ID is missing or invalid in the request body."))
        }
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(mapOf("error" to "A positive amount is required."))
        }

        var depositTransactionDto: TransactionDTO? = null
        var savedTransactionDto: TransactionDTO? = null

        try {
            // 1. สร้าง Transaction สำหรับการฝากเงิน (DEPOSIT)
            depositTransactionDto = TransactionDTO().apply {
                member = MemberDTO().apply { this.id = memberId }
                transactionType = "DEPOSIT"
                transactionAmount = amount
                transactionStatus = "Pending Payment"
            }

            // 2. บันทึก Transaction เพื่อให้ได้ ID สำหรับใช้กับ Omise
            savedTransactionDto = transactionService.saveTransaction(depositTransactionDto)

            // 3. เรียก Omise Service เพื่อสร้าง QR Code
            val omiseQrResponse = omiseService.createPromptPayQRCode(
                amount,
                savedTransactionDto.transactionId.toString()
            )

            if (omiseQrResponse != null && omiseQrResponse.containsKey("qrCodeImageBase64")) {
                val svgBase64 = omiseQrResponse["qrCodeImageBase64"]

                // 4. อัปเดตสถานะ Transaction เมื่อ QR Code ถูกสร้างแล้ว
                savedTransactionDto.transactionStatus = "QR Generated"
                transactionService.saveTransaction(savedTransactionDto)

                val response = mutableMapOf<String, Any>()
                response["transactionId"] = savedTransactionDto.transactionId!!
                response["qrCodeImageBase64"] = svgBase64!!
                return ResponseEntity.ok(response)
            } else {
                savedTransactionDto?.let {
                    it.transactionStatus = "Failed"
                    transactionService.saveTransaction(it)
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(mapOf("error" to "Failed to generate QR Code from Omise API or no QR data returned."))
            }
        } catch (e: IllegalArgumentException) {
            val failedDto = savedTransactionDto ?: depositTransactionDto
            if (failedDto?.transactionId != null) {
                try {
                    failedDto.transactionStatus = "Failed"
                    transactionService.saveTransaction(failedDto)
                } catch (ex: Exception) { /* ignore secondary save error */ }
            }
            return ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Unknown error")))
        } catch (e: RuntimeException) {
            savedTransactionDto?.let {
                try {
                    it.transactionStatus = "Failed"
                    transactionService.saveTransaction(it)
                } catch (ex: Exception) { /* ignore secondary save error */ }
            }
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Omise API Error: ${e.message}", e)
        } catch (e: Exception) {
            savedTransactionDto?.let {
                try {
                    it.transactionStatus = "Failed"
                    transactionService.saveTransaction(it)
                } catch (ex: Exception) { /* ignore secondary save error */ }
            }
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating deposit QR code: ${e.message}", e)
        }
    }
}

