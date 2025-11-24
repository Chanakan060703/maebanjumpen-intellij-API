package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.PartyRole;
import com.itsci.mju.maebanjumpen.model.Person;
import org.springframework.data.jpa.repository.EntityGraph; // ⬅️ IMPORT EntityGraph
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRoleRepository extends JpaRepository<PartyRole, Integer> {

    @EntityGraph(attributePaths = {"person"})
    List<PartyRole> findByPersonPersonId(Integer personId);
}