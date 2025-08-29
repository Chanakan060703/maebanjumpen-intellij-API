package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.HousekeeperSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousekeeperSkillRepository extends JpaRepository<HousekeeperSkill, Integer> {
    List<HousekeeperSkill> findByHousekeeperId(int housekeeperId);
}