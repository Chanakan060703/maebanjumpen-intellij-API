package com.itsci.mju.maebanjumpen.skilltype.request

import jakarta.validation.constraints.NotBlank

class CreateSkillTierRequest {
  @field:NotBlank(message = "Name period is required")
  var skillLevelName: String? = null
  @field:NotBlank(message = "Detail period is required")
  var minHiresForLevel: Int? = null
}