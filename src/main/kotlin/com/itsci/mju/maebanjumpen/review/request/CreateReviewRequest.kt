package com.itsci.mju.maebanjumpen.review.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

class CreateReviewRequest {
    @field:NotBlank(message = "reviewMessage period is required")
    var reviewMessage: String? = null

    @field:NotBlank(message = "score period is required")
    var score: Double? = null

    @field:NotBlank(message = "hireId period is required")
    @Min(value = 1, message = "hireId must be greater than 0")
    var hireId: Long = 0
}