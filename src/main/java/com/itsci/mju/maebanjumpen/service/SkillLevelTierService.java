package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.SkillLevelTierDTO;
import java.util.List;

public interface SkillLevelTierService {
    List<SkillLevelTierDTO> getAllSkillLevelTiers();
    SkillLevelTierDTO getSkillLevelTierById(Integer id);
    SkillLevelTierDTO createSkillLevelTier(SkillLevelTierDTO skillLevelTierDTO);
    SkillLevelTierDTO updateSkillLevelTier(Integer id, SkillLevelTierDTO skillLevelTierDTO);
    void deleteSkillLevelTier(Integer id);

}