package com.itsci.mju.maebanjumpen.skilltype.service.impl

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillTypeDTO
import com.itsci.mju.maebanjumpen.skilltype.repository.SkillTypeRepository
import com.itsci.mju.maebanjumpen.skilltype.request.CreateSkillTypeRequest
import com.itsci.mju.maebanjumpen.skilltype.request.UpdateSkillTypeRequest
import com.itsci.mju.maebanjumpen.skilltype.service.SkillTypeService
import com.luca.intern.common.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = true)
class SkillTypeServiceImpl @Autowired internal constructor(
    private val skillTypeRepository: SkillTypeRepository
) : SkillTypeService {

  override fun listAllSkillTypes(): List<SkillTypeDTO> {
    return skillTypeRepository.findAll().map {
      SkillTypeDTO(
        id = it.id,
        skillTypeName = it.skillTypeName,
        skillTypeDetail = it.skillTypeDetail
      )
    }


  }

  override fun getSkillTypeById(id: Long): SkillTypeDTO {
    val skillType = skillTypeRepository.findById(id)
      .orElseThrow { NotFoundException("SkillType not found with id: $id") }
    return SkillTypeDTO(
      id = skillType.id,
      skillTypeName = skillType.skillTypeName,
      skillTypeDetail = skillType.skillTypeDetail
    )
  }

  override fun createSkillType(request: CreateSkillTypeRequest): SkillTypeDTO {
    val skillType = skillTypeRepository.save(
      com.itsci.mju.maebanjumpen.entity.SkillType(
        skillTypeName = request.skillTypeName,
        skillTypeDetail = request.skillTypeDetail
      )
    )
    return SkillTypeDTO(
      id = skillType.id,
      skillTypeName = skillType.skillTypeName,
      skillTypeDetail = skillType.skillTypeDetail
    )
  }

  override fun updateSkillType(request: UpdateSkillTypeRequest): SkillTypeDTO {
    val skillType = skillTypeRepository.findById(request.id)
      .orElseThrow { NotFoundException("SkillType not found with id: ${request.id}") }

    skillType.skillTypeName = request.skillTypeName
    skillType.skillTypeDetail = request.skillTypeDetail
    val updatedSkillType = skillTypeRepository.save(skillType)
    return SkillTypeDTO(
      id = updatedSkillType.id,
      skillTypeName = updatedSkillType.skillTypeName,
      skillTypeDetail = updatedSkillType.skillTypeDetail
    )
  }

  override fun deleteSkillType(id: Long) : Boolean{
    val skillType = skillTypeRepository.findById(id)
      .orElseThrow { NotFoundException("SkillType not found with id: $id") }
    skillTypeRepository.delete(skillType)

    return true
  }
}
