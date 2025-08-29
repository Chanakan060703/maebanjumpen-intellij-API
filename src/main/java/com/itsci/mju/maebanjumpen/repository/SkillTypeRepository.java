package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillTypeRepository extends JpaRepository<SkillType, Integer> {
}