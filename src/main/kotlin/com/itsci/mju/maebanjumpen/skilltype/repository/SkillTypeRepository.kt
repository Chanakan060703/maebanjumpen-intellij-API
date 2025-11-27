package com.itsci.mju.maebanjumpen.skilltype.repository

import com.itsci.mju.maebanjumpen.entity.SkillType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillTypeRepository : JpaRepository<SkillType, Long>{

}
