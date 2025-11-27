package com.itsci.mju.maebanjumpen.report.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

class UpdateReportRequest {
  @field:NotBlank(message = "Id is required")
  @Min(value = 1, message = "Id must be greater than 0")
    var id: Long = 0
  @field:NotBlank(message = "reportTitle period is required")
    var reportTitle: String? = null
  @field:NotBlank(message = "reportMessage period is required")
    var reportMessage: String? = null
  @field:NotBlank(message = "reportStatus period is required")
    var reportStatus: String? = null
  @field:NotBlank(message = "reporterId period is required")
  @Min(value = 1, message = "reporterId must be greater than 0")
    var reporterId: Long = 0

  @Min(value = 1, message = "reportType is required")
    var penaltyId: Long = 0
}