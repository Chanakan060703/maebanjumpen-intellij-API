package com.itsci.mju.maebanjumpen.housekeeperskill.repository

import com.itsci.mju.maebanjumpen.entity.HousekeeperSkill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HousekeeperSkillRepository : JpaRepository<HousekeeperSkill, Int> {

    fun findByHousekeeperIdAndSkillTypeSkillTypeId(housekeeperId: Int, skillTypeId: Int): Optional<HousekeeperSkill>

    fun findByHousekeeperId(housekeeperId: Int): Optional<HousekeeperSkill>
}

