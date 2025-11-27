package com.itsci.mju.maebanjumpen.skilltype.repository

import com.itsci.mju.maebanjumpen.entity.SkillLevelTier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SkillLevelTierRepository : JpaRepository<SkillLevelTier, Long> {
    fun findByMinHiresForLevel(minHiresForLevel: Int): Optional<SkillLevelTier>
    fun findBySkillLevelName(skillLevelName: String): Optional<SkillLevelTier>
}

