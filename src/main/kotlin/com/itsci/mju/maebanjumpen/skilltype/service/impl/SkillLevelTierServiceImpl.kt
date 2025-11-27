package com.itsci.mju.maebanjumpen.skilltype.service.impl

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillLevelTierDTO
import com.itsci.mju.maebanjumpen.skilltype.repository.SkillLevelTierRepository
import com.itsci.mju.maebanjumpen.skilltype.request.CreateSkillTierRequest
import com.itsci.mju.maebanjumpen.skilltype.request.UpdateSkillTierRequest
import com.itsci.mju.maebanjumpen.skilltype.service.SkillLevelTierService
import com.luca.intern.common.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SkillLevelTierServiceImpl @Autowired internal constructor(
    private val skillLevelTierRepository: SkillLevelTierRepository
) : SkillLevelTierService {
  override fun listAllSkillLevelTiers(): List<SkillLevelTierDTO> {
    return skillLevelTierRepository.findAll().map {
      SkillLevelTierDTO(
        id = it.id,
        skillLevelName = it.skillLevelName,
        minHiresForLevel = it.minHiresForLevel
      )
    }
  }

  override fun getSkillLevelTierById(id: Long): SkillLevelTierDTO? {
    val skillLevelTier = skillLevelTierRepository.findById(id)
      .orElseThrow { NotFoundException("SkillLevelTier not found with id: $id") }
    return SkillLevelTierDTO(
        id = skillLevelTier.id,
        skillLevelName = skillLevelTier.skillLevelName,
        minHiresForLevel = skillLevelTier.minHiresForLevel
      )
  }

  override fun createSkillLevelTier(request: CreateSkillTierRequest): SkillLevelTierDTO {
    val  skillLevelTier = skillLevelTierRepository.save(
      com.itsci.mju.maebanjumpen.entity.SkillLevelTier(
        skillLevelName = request.skillLevelName,
        minHiresForLevel = request.minHiresForLevel
      )
    )
    return SkillLevelTierDTO(
      id = skillLevelTier.id,
      skillLevelName = skillLevelTier.skillLevelName,
      minHiresForLevel = skillLevelTier.minHiresForLevel
    )
  }

  override fun updateSkillLevelTier(request: UpdateSkillTierRequest): SkillLevelTierDTO {
    val skillLevelTier = skillLevelTierRepository.findById(request.id)
      .orElseThrow { NotFoundException("SkillLevelTier not found with id: ${request.id}") }

    skillLevelTier.skillLevelName = request.skillLevelName
    skillLevelTier.minHiresForLevel = request.minHiresForLevel
    val updatedSkillLevelTier = skillLevelTierRepository.save(skillLevelTier)
    return SkillLevelTierDTO(
      id = updatedSkillLevelTier.id,
      skillLevelName = updatedSkillLevelTier.skillLevelName,
      minHiresForLevel = updatedSkillLevelTier.minHiresForLevel
    )
  }

  override fun deleteSkillLevelTier(id: Long): Boolean {
    val skillLevelTier = skillLevelTierRepository.findById(id)
      .orElseThrow { NotFoundException("SkillLevelTier not found with id: $id") }
    skillLevelTierRepository.delete(skillLevelTier)

    return true
  }


}
