package com.itsci.mju.maebanjumpen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillTypeDTO {
    private Integer skillTypeId;
    private String skillTypeName;
    private String skillTypeDetail;
}