package com.itsci.mju.maebanjumpen.partyrole.dto;

import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO;
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
    private String dailyRate;

    private List<Integer> hireIds;

    private Set<HousekeeperSkillDTO> housekeeperSkills;

    @Override
    public String getType() {
        return "housekeeper";
    }
}