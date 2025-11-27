package com.itsci.mju.maebanjumpen.person.repository

import com.itsci.mju.maebanjumpen.entity.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PersonRepository : JpaRepository<Person, Long> {
    fun findByLoginUsername(username: String): Optional<Person>
}

