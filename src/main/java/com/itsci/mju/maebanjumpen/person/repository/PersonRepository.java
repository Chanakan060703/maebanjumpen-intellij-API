package com.itsci.mju.maebanjumpen.person.repository;

import com.itsci.mju.maebanjumpen.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // 💡 ต้อง Import คลาส Optional

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByLoginUsername(String username);

}