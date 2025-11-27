package com.itsci.mju.maebanjumpen.partyrole.repository

import com.itsci.mju.maebanjumpen.entity.Member
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface MemberRepository : JpaRepository<Member, Int> {

    @EntityGraph(value = "Member.fullDetails", type = EntityGraph.EntityGraphType.LOAD)
    override fun findById(id: Int): Optional<Member>

    @EntityGraph(value = "Member.fullDetails", type = EntityGraph.EntityGraphType.LOAD)
    override fun findAll(): List<Member>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.id = :id")
    fun findByIdWithLock(id: Int): Optional<Member>
}

