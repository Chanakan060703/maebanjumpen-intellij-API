package com.itsci.mju.maebanjumpen.hire.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.itsci.mju.maebanjumpen.partyrole.dto.HirerDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import com.itsci.mju.maebanjumpen.review.dto.ReviewDTO
import com.itsci.mju.maebanjumpen.skilltype.dto.SkillTypeDTO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class HireDTO(
    var id: Long? = 0,
    var hireName: String? = null,
    var hireDetail: String? = null,
    var paymentAmount: Double? = null,
    var hireDate: LocalDateTime? = null,
    var startDate: LocalDate? = null,
    var startTime: LocalTime? = null,
    var endTime: LocalTime? = null,
    var location: String? = null,
    var jobStatus: String? = null,
    var progressionImageUrls: List<String>? = null,
    var hirer: HirerDTO? = null,
    var housekeeper: HousekeeperDTO? = null,
    var skillType: SkillTypeDTO? = null,
    var review: ReviewDTO? = null
)

