package com.itsci.mju.maebanjumpen.skilltype.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

class UpdateSkillTypeRequest {
  @field:NotBlank(message = "Id is required")
  @Min(value = 1, message = "Name period is required")
  var id: Long = 0

  @field:NotBlank(message = "Name period is required")
  var skillTypeName: String? = null

  @field:NotBlank(message = "Detail period is required")
  var skillTypeDetail: String? = null
}