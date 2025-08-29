package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.PartyRole;
import java.util.List;

public interface PartyRoleService {
    PartyRole savePartyRole(PartyRole partyRole);
    PartyRole getPartyRoleById(int id);
    List<PartyRole> getAllPartyRoles();
    PartyRole updatePartyRole(int id, PartyRole partyRole);
    void deletePartyRole(int id);
}
