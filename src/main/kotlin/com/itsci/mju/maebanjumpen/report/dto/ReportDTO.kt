package com.itsci.mju.maebanjumpen.report.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO
import com.itsci.mju.maebanjumpen.penalty.dto.PenaltyDTO
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReportDTO(
    var id: Long? = 0,
    var reportTitle: String? = null,
    var reportMessage: String? = null,
    var reportDate: LocalDateTime? = null,
    var reportStatus: String? = null,
    var reporter: PartyRoleDTO? = null,
    var penalty: PenaltyDTO? = null,
    var hire: HireDTO? = null
)

