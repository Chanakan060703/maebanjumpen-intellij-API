package com.itsci.mju.maebanjumpen.partyrole.repository

import com.itsci.mju.maebanjumpen.entity.Hirer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HirerRepository : JpaRepository<Hirer, Int> {

    @Query("SELECT h FROM Hirer h JOIN FETCH h.person p LEFT JOIN FETCH p.login l LEFT JOIN FETCH h.hires")
    override fun findAll(): List<Hirer>

    @Query("SELECT h FROM Hirer h JOIN FETCH h.person p LEFT JOIN FETCH p.login l LEFT JOIN FETCH h.hires hs WHERE h.id = :id")
    override fun findById(@Param("id") id: Int): Optional<Hirer>
}

