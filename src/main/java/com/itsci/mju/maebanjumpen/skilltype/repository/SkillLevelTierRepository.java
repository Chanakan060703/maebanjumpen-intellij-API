package com.itsci.mju.maebanjumpen.skilltype.repository;

import com.itsci.mju.maebanjumpen.entity.SkillLevelTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillLevelTierRepository extends JpaRepository<SkillLevelTier, Integer> {
    Optional<SkillLevelTier> findByMinHiresForLevel(Integer minHiresForLevel);
    Optional<SkillLevelTier> findBySkillLevelName(String skillLevelName);
}