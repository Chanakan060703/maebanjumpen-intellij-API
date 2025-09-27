package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.PartyRoleDTO;
import com.itsci.mju.maebanjumpen.model.PartyRole;
import java.util.List;

public interface PartyRoleService {
    PartyRoleDTO savePartyRole(PartyRoleDTO partyRoleDto);
    PartyRoleDTO getPartyRoleById(int id);
    List<PartyRoleDTO> getAllPartyRoles();
    PartyRoleDTO updatePartyRole(int id, PartyRoleDTO partyRoleDto);
    void deletePartyRole(int id);
}
