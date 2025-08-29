package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.HousekeeperSkill;
import com.itsci.mju.maebanjumpen.repository.HousekeeperSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HousekeeperSkillServiceImpl implements HousekeeperSkillService {

    @Autowired
    private HousekeeperSkillRepository housekeeperSkillRepository;

    @Override
    public List<HousekeeperSkill> getAllHousekeeperSkills() {
        return housekeeperSkillRepository.findAll();
    }

    @Override
    public HousekeeperSkill getHousekeeperSkillById(int id) {
        return housekeeperSkillRepository.findById(id).orElse(null);
    }

    @Override
    public HousekeeperSkill saveHousekeeperSkill(HousekeeperSkill housekeeperSkill) {
        return housekeeperSkillRepository.save(housekeeperSkill);
    }

    @Override
    public void deleteHousekeeperSkill(int id) {
        housekeeperSkillRepository.deleteById(id);
    }

    @Override
    public List<HousekeeperSkill> getSkillsByHousekeeperId(int housekeeperId) {
        return housekeeperSkillRepository.findByHousekeeperId(housekeeperId);
    }

    @Override
    public HousekeeperSkill updateHousekeeperSkill(int id, HousekeeperSkill skill) {
        HousekeeperSkill existingHousekeeperSkill = housekeeperSkillRepository.getReferenceById(skill.getSkillId());
        if (existingHousekeeperSkill == null){
            throw new RuntimeException("HousekeeperSkill not found");
        }
        return housekeeperSkillRepository.save(skill);
    }
}