package com.itsci.mju.maebanjumpen.skilltype.request

import jakarta.validation.constraints.NotBlank

class CreateSkillTypeRequest {
  @field:NotBlank(message = "skillTypeName period is required")
  var skillTypeName: String? = null
  @field:NotBlank(message = "skillTypeDetail period is required")
  var skillTypeDetail: String? = null
}