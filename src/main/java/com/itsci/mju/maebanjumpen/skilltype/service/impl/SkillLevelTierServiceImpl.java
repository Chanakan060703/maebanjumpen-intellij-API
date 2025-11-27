package com.itsci.mju.maebanjumpen.skilltype.service.impl;

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillLevelTierDTO;
import com.itsci.mju.maebanjumpen.mapper.SkillLevelTierMapper;
import com.itsci.mju.maebanjumpen.entity.SkillLevelTier;
import com.itsci.mju.maebanjumpen.skilltype.repository.SkillLevelTierRepository;
import com.itsci.mju.maebanjumpen.skilltype.service.SkillLevelTierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillLevelTierServiceImpl implements SkillLevelTierService {

    private final SkillLevelTierRepository skillLevelTierRepository;
    private final SkillLevelTierMapper skillLevelTierMapper;

    @Override
    public List<SkillLevelTierDTO> getAllSkillLevelTiers() {
        List<SkillLevelTier> tiers = skillLevelTierRepository.findAll();
        return skillLevelTierMapper.toDtoList(tiers);
    }

    @Override
    public SkillLevelTierDTO getSkillLevelTierById(Integer id) {
        return skillLevelTierRepository.findById(id)
                .map(skillLevelTierMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public SkillLevelTierDTO createSkillLevelTier(SkillLevelTierDTO skillLevelTierDTO) {
        SkillLevelTier tier = skillLevelTierMapper.toEntity(skillLevelTierDTO);
        SkillLevelTier savedTier = skillLevelTierRepository.save(tier);
        return skillLevelTierMapper.toDto(savedTier);
    }

    @Override
    @Transactional
    public SkillLevelTierDTO updateSkillLevelTier(Integer id, SkillLevelTierDTO skillLevelTierDTO) {
        SkillLevelTier existingTier = skillLevelTierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SkillLevelTier with ID " + id + " not found."));

        // Update fields from DTO to Entity
        Optional.ofNullable(skillLevelTierDTO.getSkillLevelName()).ifPresent(existingTier::setSkillLevelName);
        Optional.ofNullable(skillLevelTierDTO.getMinHiresForLevel()).ifPresent(existingTier::setMinHiresForLevel);

        SkillLevelTier updatedTier = skillLevelTierRepository.save(existingTier);
        return skillLevelTierMapper.toDto(updatedTier);
    }

    @Override
    @Transactional
    public void deleteSkillLevelTier(Integer id) {
        if (!skillLevelTierRepository.existsById(id)) {
            throw new RuntimeException("SkillLevelTier with ID " + id + " not found.");
        }
        skillLevelTierRepository.deleteById(id);
    }
}