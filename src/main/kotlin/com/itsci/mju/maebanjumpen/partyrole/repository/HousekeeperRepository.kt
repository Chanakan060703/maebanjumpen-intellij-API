package com.itsci.mju.maebanjumpen.partyrole.repository

import com.itsci.mju.maebanjumpen.entity.Housekeeper
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HousekeeperRepository : JpaRepository<Housekeeper, Int> {

    @Query("""
        SELECT DISTINCT h FROM Housekeeper h 
        JOIN FETCH h.person p 
        LEFT JOIN FETCH p.login l 
        LEFT JOIN FETCH h.housekeeperSkills hsk 
        LEFT JOIN FETCH h.hires hi 
        LEFT JOIN FETCH hi.review r 
        LEFT JOIN FETCH hi.hirer hirer_obj 
        LEFT JOIN FETCH hirer_obj.person hp 
        LEFT JOIN FETCH hp.login hpl
    """)
    fun findAllWithPersonLoginAndSkills(): List<Housekeeper>

    @Query("""
        SELECT DISTINCT h FROM Housekeeper h 
        LEFT JOIN FETCH h.person p 
        LEFT JOIN FETCH p.login l 
        LEFT JOIN FETCH h.housekeeperSkills hsk 
        LEFT JOIN FETCH hsk.skillType st 
        LEFT JOIN FETCH hsk.skillLevelTier slt 
        LEFT JOIN FETCH h.hires hi 
        LEFT JOIN FETCH hi.review r 
        WHERE h.id = :id
    """)
    fun findByIdWithAllDetails(@Param("id") id: Int): Optional<Housekeeper>

    @Query("SELECT AVG(r.score) FROM Review r JOIN r.hire h WHERE h.housekeeper.id = :housekeeperId AND (h.jobStatus = 'Completed' OR h.jobStatus = 'Reviewed')")
    fun calculateAverageRatingByHousekeeperId(@Param("housekeeperId") housekeeperId: Int): Double?

    @Query("""
        SELECT DISTINCT h FROM Housekeeper h 
        JOIN FETCH h.person p 
        LEFT JOIN FETCH p.login l 
        LEFT JOIN FETCH h.housekeeperSkills hsk 
        LEFT JOIN FETCH h.hires hi 
        LEFT JOIN FETCH hi.review r 
        LEFT JOIN FETCH hi.hirer hirer_obj 
        LEFT JOIN FETCH hirer_obj.person hp 
        LEFT JOIN FETCH hp.login hpl 
        WHERE h.statusVerify = :statusVerify
    """)
    fun findByStatusVerifyWithDetails(@Param("statusVerify") statusVerify: String): List<Housekeeper>

    @Query("""
        SELECT DISTINCT h FROM Housekeeper h 
        JOIN FETCH h.person p 
        LEFT JOIN FETCH p.login l 
        LEFT JOIN FETCH h.housekeeperSkills hsk 
        LEFT JOIN FETCH h.hires hi 
        LEFT JOIN FETCH hi.review r 
        LEFT JOIN FETCH hi.hirer hirer_obj 
        LEFT JOIN FETCH hirer_obj.person hp 
        LEFT JOIN FETCH hp.login hpl 
        WHERE h.statusVerify = 'PENDING' OR h.statusVerify IS NULL
    """)
    fun findNotVerifiedOrNullStatusHousekeepersWithDetails(): List<Housekeeper>
}

