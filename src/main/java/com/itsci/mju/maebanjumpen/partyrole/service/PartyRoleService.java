package com.itsci.mju.maebanjumpen.partyrole.service;

import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO;

import java.util.List;

public interface PartyRoleService {
    PartyRoleDTO savePartyRole(PartyRoleDTO partyRoleDto);
    PartyRoleDTO getPartyRoleById(int id);
    List<PartyRoleDTO> getAllPartyRoles();
    PartyRoleDTO updatePartyRole(int id, PartyRoleDTO partyRoleDto);
    void deletePartyRole(int id);
}
