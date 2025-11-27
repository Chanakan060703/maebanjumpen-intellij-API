package com.itsci.mju.maebanjumpen.review.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

class UpdateReviewRequest {
  @field:NotBlank(message = "Id is required")
  @Min(value = 1, message = "Id must be greater than 0")
  var id: Long = 0
  @field:NotBlank(message = "reviewMessage period is required")
  val reviewMessage: String? = null
  @field:NotBlank(message = "score period is required")
  val score: Double? = null
}