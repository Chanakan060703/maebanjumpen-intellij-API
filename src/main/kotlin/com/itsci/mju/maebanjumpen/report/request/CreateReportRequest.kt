package com.itsci.mju.maebanjumpen.report.request

import jakarta.validation.constraints.NotBlank

class CreateReportRequest {
    @field:NotBlank(message = "reportTitle period is required")
    var reportTitle: String? = null

    @field:NotBlank(message = "reportMessage period is required")
    var reportMessage: String? = null

    @field:NotBlank(message = "reportStatus period is required")
    var reportStatus: String? = null

    @field:NotBlank(message = "reportDate period is required")
    var reportDate: String? = null

    @field:NotBlank(message = "reporterId period is required")
    var reporterId: Long = 0

    @field:NotBlank(message = "hireId period is required")
    var hireId: Long = 0
}