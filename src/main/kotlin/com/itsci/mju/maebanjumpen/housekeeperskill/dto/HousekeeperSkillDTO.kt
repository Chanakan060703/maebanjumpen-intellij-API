package com.itsci.mju.maebanjumpen.housekeeperskill.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.itsci.mju.maebanjumpen.skilltype.dto.SkillLevelTierDTO
import com.itsci.mju.maebanjumpen.skilltype.dto.SkillTypeDTO

data class HousekeeperSkillDTO(
    @JsonProperty("skillId")
    var id: Long? = 0,

    // ฟิลด์ ID ที่ถูกแมปจาก HousekeeperSkillMapper
    @JsonProperty("skillLevelTierId")
    var skillLevelTierId: Int? = null,

    @JsonProperty("housekeeperId")
    var housekeeperId: Int? = null,

    @JsonProperty("skillTypeId")
    var skillTypeId: Int? = null,

    @JsonProperty("skillLevelTier")
    var skillLevelTier: SkillLevelTierDTO? = null,

    @JsonProperty("skillType")
    var skillType: SkillTypeDTO? = null,

    @JsonProperty("pricePerDay")
    var pricePerDay: Double? = null,

    @JsonProperty("totalHiresCompleted")
    var totalHiresCompleted: Int? = null
)

