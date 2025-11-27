package com.itsci.mju.maebanjumpen.skilltype.service

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillTypeDTO
import com.itsci.mju.maebanjumpen.skilltype.request.CreateSkillTypeRequest
import com.itsci.mju.maebanjumpen.skilltype.request.UpdateSkillTypeRequest

interface SkillTypeService {
    fun listAllSkillTypes(): List<SkillTypeDTO>

    fun getSkillTypeById(id: Long): SkillTypeDTO

    fun createSkillType(request: CreateSkillTypeRequest): SkillTypeDTO

    fun updateSkillType(
      request: UpdateSkillTypeRequest
    ): SkillTypeDTO

    fun deleteSkillType(id: Long): Boolean
}

