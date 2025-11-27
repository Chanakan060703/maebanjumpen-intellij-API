package com.itsci.mju.maebanjumpen.partyrole.repository;

import com.itsci.mju.maebanjumpen.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    @EntityGraph(value = "Member.fullDetails", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Member> findById(Integer id); // Override default findById to use EntityGraph


    @EntityGraph(value = "Member.fullDetails", type = EntityGraph.EntityGraphType.LOAD)
    List<Member> findAll(); // Override default findAll to use EntityGraph

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.id = :id")
    Optional<Member> findByIdWithLock(Integer id);


}