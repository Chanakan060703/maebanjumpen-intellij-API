package com.itsci.mju.maebanjumpen.housekeeperskill.service

import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import java.util.Optional

interface HousekeeperSkillService {
    fun getAllHousekeeperSkills(): List<HousekeeperDTO>
    fun getHousekeeperSkillById(id: Int): HousekeeperSkillDTO?
    fun saveHousekeeperSkill(housekeeperSkillDto: HousekeeperSkillDTO): HousekeeperSkillDTO
    fun deleteHousekeeperSkill(id: Int)
    fun getSkillsByHousekeeperId(housekeeperId: Int): Optional<HousekeeperSkillDTO>
    fun updateHousekeeperSkill(id: Int, skillDto: HousekeeperSkillDTO): HousekeeperSkillDTO
    fun updateSkillLevelAndHiresCompleted(housekeeperId: Int, skillTypeId: Int)
    fun findByHousekeeperIdAndSkillTypeId(housekeeperId: Int, skillTypeId: Int): Optional<HousekeeperSkillDTO>
}

