package com.itsci.mju.maebanjumpen.partyrole.dto

import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO

open class HousekeeperDTO : MemberDTO() {
    var photoVerifyUrl: String? = null
    var statusVerify: String? = null
    var rating: Double? = null
    var dailyRate: String? = null
    var hireIds: List<Int>? = null
    var housekeeperSkills: Set<HousekeeperSkillDTO>? = null

    override fun getType(): String = "housekeeper"
}

