package com.itsci.mju.maebanjumpen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillLevelTierDTO {
    private Integer id;
    private String skillLevelName;
    private Integer minHiresForLevel;
    private Double priceMultiplier;
    private Double maxPricePerHourLimit;
    private Double minPricePerHourLimit;
}