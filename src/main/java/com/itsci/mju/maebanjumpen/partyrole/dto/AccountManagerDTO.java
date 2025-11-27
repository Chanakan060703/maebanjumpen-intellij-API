package com.itsci.mju.maebanjumpen.partyrole.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountManagerDTO extends PartyRoleDTO {
    private Integer managerID;

    @Override
    public String getType() {
        return "accountManager";
    }
}