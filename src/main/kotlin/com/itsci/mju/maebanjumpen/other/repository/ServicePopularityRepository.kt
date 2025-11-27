package com.itsci.mju.maebanjumpen.other.repository

import com.itsci.mju.maebanjumpen.entity.SkillType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ServicePopularityRepository : JpaRepository<SkillType, Int> {

    @Query("""
        SELECT st.skillTypeName, AVG(r.score), COUNT(r.reviewId) 
        FROM SkillType st 
        JOIN HousekeeperSkill hsk ON st.skillTypeId = hsk.skillType.skillTypeId 
        JOIN hsk.housekeeper hk 
        JOIN Hire h ON hk.id = h.housekeeper.id 
        LEFT JOIN Review r ON h.hireId = r.hire.hireId 
        WHERE h.jobStatus = 'Completed' AND r.score IS NOT NULL 
        GROUP BY st.skillTypeName
    """)
    fun findServicePopularityData(): List<Array<Any>>
}

