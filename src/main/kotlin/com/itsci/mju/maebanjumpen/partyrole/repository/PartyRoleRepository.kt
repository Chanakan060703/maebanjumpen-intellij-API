package com.itsci.mju.maebanjumpen.partyrole.repository

import com.itsci.mju.maebanjumpen.entity.PartyRole
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartyRoleRepository : JpaRepository<PartyRole, Long> {

    @EntityGraph(attributePaths = ["person"])
    fun findByPersonId(personId: Long): List<PartyRole>
}

