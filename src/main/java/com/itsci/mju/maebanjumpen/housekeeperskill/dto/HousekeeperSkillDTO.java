package com.itsci.mju.maebanjumpen.housekeeperskill.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itsci.mju.maebanjumpen.skilltype.dto.SkillLevelTierDTO;
import com.itsci.mju.maebanjumpen.skilltype.dto.SkillTypeDTO;
import lombok.Data;

@Data
public class HousekeeperSkillDTO {

    @JsonProperty("skillId")
    private Integer skillId;

    // ฟิลด์ ID ที่ถูกแมปจาก HousekeeperSkillMapper
    @JsonProperty("skillLevelTierId")
    private Integer skillLevelTierId;

    @JsonProperty("housekeeperId")
    private Integer housekeeperId;

    @JsonProperty("skillTypeId")
    private Integer skillTypeId;

    @JsonProperty("skillLevelTier")
    private SkillLevelTierDTO skillLevelTier;

    @JsonProperty("skillType")
    private SkillTypeDTO skillType;

    @JsonProperty("pricePerDay")
    private Double pricePerDay;

    @JsonProperty("totalHiresCompleted")
    private Integer totalHiresCompleted;
}
