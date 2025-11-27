package com.itsci.mju.maebanjumpen.hire.repository

import com.itsci.mju.maebanjumpen.entity.Hire
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HireRepository : JpaRepository<Hire, Long> {

    @Query("""
        SELECT DISTINCT h FROM Hire h 
        LEFT JOIN FETCH h.hirer hr 
        LEFT JOIN FETCH hr.person hrp 
        LEFT JOIN FETCH h.housekeeper hk 
        LEFT JOIN FETCH hk.person hkp 
        LEFT JOIN FETCH h.skillType st 
        LEFT JOIN FETCH h.review r 
        LEFT JOIN FETCH h.progressionImageUrls
    """)
    fun findAllWithDetails(): List<Hire>

    @Query("""
        SELECT DISTINCT h FROM Hire h
        LEFT JOIN FETCH h.hirer hr
        LEFT JOIN FETCH hr.person hrp
        LEFT JOIN FETCH h.housekeeper hk
        LEFT JOIN FETCH hk.person hkp
        LEFT JOIN FETCH h.skillType st
        LEFT JOIN FETCH h.review r
        LEFT JOIN FETCH h.progressionImageUrls
        WHERE h.hireId = :id
    """)
    fun fetchByIdWithAllDetails(@Param("id") id: Long): Optional<Hire>

    @Query("""
        SELECT DISTINCT h FROM Hire h
        LEFT JOIN FETCH h.hirer hr
        LEFT JOIN FETCH hr.person hrp
        LEFT JOIN FETCH h.housekeeper hk
        LEFT JOIN FETCH hk.person hkp
        LEFT JOIN FETCH h.skillType st
        LEFT JOIN FETCH h.review r
        LEFT JOIN FETCH h.progressionImageUrls
        WHERE hr.id = :hirerId
    """)
    fun findByHirerIdWithDetails(@Param("hirerId") hirerId: Long): List<Hire>

    @Query("""
        SELECT DISTINCT h FROM Hire h
        LEFT JOIN FETCH h.hirer hr
        LEFT JOIN FETCH hr.person hrp
        LEFT JOIN FETCH h.housekeeper hk
        LEFT JOIN FETCH hk.person hkp
        LEFT JOIN FETCH h.skillType st
        LEFT JOIN FETCH h.review r
        LEFT JOIN FETCH h.progressionImageUrls
        WHERE hk.id = :housekeeperId
    """)
    fun findByHousekeeperIdWithDetails(@Param("housekeeperId") housekeeperId: Long): List<Hire>

    @Query("""
        SELECT DISTINCT h FROM Hire h
        LEFT JOIN FETCH h.hirer hr
        LEFT JOIN FETCH hr.person hrp
        LEFT JOIN FETCH h.housekeeper hk
        LEFT JOIN FETCH hk.person hkp
        LEFT JOIN FETCH h.skillType st
        LEFT JOIN FETCH h.review r
        LEFT JOIN FETCH h.progressionImageUrls
        WHERE hk.id = :housekeeperId AND h.jobStatus = :jobStatus
    """)
    fun findByHousekeeperIdAndJobStatusWithDetails(
        @Param("housekeeperId") housekeeperId: Long,
        @Param("jobStatus") jobStatus: String
    ): List<Hire>
}

