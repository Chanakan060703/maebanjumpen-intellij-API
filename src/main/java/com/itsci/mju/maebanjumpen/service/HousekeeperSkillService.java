package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.HousekeeperSkill;

import java.util.List;

public interface HousekeeperSkillService {
    List<HousekeeperSkill> getAllHousekeeperSkills();
    HousekeeperSkill getHousekeeperSkillById(int id);
    HousekeeperSkill saveHousekeeperSkill(HousekeeperSkill housekeeperSkill);
    void deleteHousekeeperSkill(int id);
    List<HousekeeperSkill> getSkillsByHousekeeperId(int housekeeperId);

    HousekeeperSkill updateHousekeeperSkill(int id, HousekeeperSkill skill);
}