package com.itsci.mju.maebanjumpen.review.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReviewDTO(
  var id: Long? = 0,
  var reviewMessage: String? = null,
  var score: Double? = null,
  var reviewDate: LocalDateTime? = null,
  var hires: HireDTO? = null
)

