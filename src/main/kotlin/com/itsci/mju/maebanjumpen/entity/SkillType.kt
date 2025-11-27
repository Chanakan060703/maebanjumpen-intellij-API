package com.itsci.mju.maebanjumpen.entity

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillTypeDTO
import jakarta.persistence.*

@Entity
@Table(name = "skill_type")
data class SkillType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,

    @Column(name = "skill_type_name")
    var skillTypeName: String? = null,

    @Column(name = "skill_type_detail")
    var skillTypeDetail: String? = null
){
  fun  toSkillTypeDTO(id: Long?): SkillTypeDTO {
        return SkillTypeDTO(
          id =  id,
          skillTypeName = skillTypeName,
          skillTypeDetail = skillTypeDetail)
    }
}

