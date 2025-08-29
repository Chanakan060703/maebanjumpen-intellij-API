package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.PartyRole;
import com.itsci.mju.maebanjumpen.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRoleRepository extends JpaRepository<PartyRole, Integer> {
    List<PartyRole> findByPerson(Person person);
}