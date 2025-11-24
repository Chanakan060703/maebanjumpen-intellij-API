package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // 💡 ต้อง Import คลาส Optional

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByLoginUsername(String username);

}