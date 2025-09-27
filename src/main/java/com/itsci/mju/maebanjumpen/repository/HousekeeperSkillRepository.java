package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.HousekeeperSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HousekeeperSkillRepository extends JpaRepository<HousekeeperSkill, Integer> {

    Optional<HousekeeperSkill> findByHousekeeperIdAndSkillTypeSkillTypeId(Integer housekeeperId, Integer skillTypeId);

    Optional<HousekeeperSkill> findByHousekeeperId(Integer housekeeperId);

    // หรือใช้ @Query เพื่อระบุ JPQL query ให้ชัดเจน
    // @Query("SELECT hks FROM HousekeeperSkill hks WHERE hks.housekeeper.id = :housekeeperId AND hks.skillType.skillTypeId = :skillTypeId")
    // Optional<HousekeeperSkill> findByHousekeeperIdAndSkillTypeId(@Param("housekeeperId") Integer housekeeperId, @Param("skillTypeId") Integer skillTypeId);
}