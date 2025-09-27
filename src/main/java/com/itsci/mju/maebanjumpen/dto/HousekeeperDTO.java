package com.itsci.mju.maebanjumpen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HousekeeperDTO extends MemberDTO {
    private String photoVerifyUrl;
    private String statusVerify;
    private Double rating;
    private Double dailyRate;

    private List<Integer> hireIds;

    private Set<HousekeeperSkillDTO> housekeeperSkills;

    @Override
    public String getType() {
        return "housekeeper";
    }
}