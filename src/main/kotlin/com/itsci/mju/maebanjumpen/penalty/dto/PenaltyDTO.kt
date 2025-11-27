package com.itsci.mju.maebanjumpen.penalty.dto

import java.time.LocalDateTime

data class PenaltyDTO(
    var id: Long? = 0,
    var penaltyType: String? = null,
    var penaltyDetail: String? = null,
    var penaltyDate: LocalDateTime? = null,
    var penaltyStatus: String? = null,
    // เพิ่ม Report ID เพื่อใช้ในการเชื่อมโยง/อัปเดตตรรกะใน Service
    var reportId: Int? = null
)

