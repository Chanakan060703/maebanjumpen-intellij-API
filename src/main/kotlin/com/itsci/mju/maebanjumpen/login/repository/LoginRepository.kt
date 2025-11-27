package com.itsci.mju.maebanjumpen.login.repository

import com.itsci.mju.maebanjumpen.entity.Login
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface LoginRepository : JpaRepository<Login, String> {
    fun findByUsername(username: String): Optional<Login>
    fun existsByUsername(username: String): Boolean
    fun deleteByUsername(username: String)
}

