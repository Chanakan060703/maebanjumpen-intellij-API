package com.itsci.mju.maebanjumpen.skilltype.service

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillLevelTierDTO
import com.itsci.mju.maebanjumpen.skilltype.request.CreateSkillTierRequest
import com.itsci.mju.maebanjumpen.skilltype.request.UpdateSkillTierRequest

interface SkillLevelTierService {
    fun listAllSkillLevelTiers(): List<SkillLevelTierDTO>
    fun getSkillLevelTierById(id: Long): SkillLevelTierDTO?
    fun createSkillLevelTier(request: CreateSkillTierRequest): SkillLevelTierDTO
    fun updateSkillLevelTier(request: UpdateSkillTierRequest): SkillLevelTierDTO
    fun deleteSkillLevelTier(id: Long): Boolean
}

