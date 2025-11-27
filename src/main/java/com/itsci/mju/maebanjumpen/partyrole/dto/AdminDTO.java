package com.itsci.mju.maebanjumpen.partyrole.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO extends PartyRoleDTO {
    private String adminStatus;

    @Override
    public String getType() {
        return "admin";
    }
}