package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.HousekeeperDTO;
import com.itsci.mju.maebanjumpen.dto.HousekeeperSkillDTO;

import java.util.List;
import java.util.Optional;

public interface HousekeeperSkillService {
    List<HousekeeperDTO> getAllHousekeeperSkills();
    HousekeeperSkillDTO getHousekeeperSkillById(int id);
    HousekeeperSkillDTO saveHousekeeperSkill(HousekeeperSkillDTO housekeeperSkillDto);
    void deleteHousekeeperSkill(int id);
    Optional<HousekeeperSkillDTO> getSkillsByHousekeeperId(int housekeeperId);
    HousekeeperSkillDTO updateHousekeeperSkill(int id, HousekeeperSkillDTO skillDto);
    void updateSkillLevelAndHiresCompleted(Integer housekeeperId, Integer skillTypeId);
    Optional<HousekeeperSkillDTO> findByHousekeeperIdAndSkillTypeId(Integer housekeeperId, Integer skillTypeId);
}