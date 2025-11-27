package com.itsci.mju.maebanjumpen.login.repository;

import com.itsci.mju.maebanjumpen.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Login, String> {
    Optional<Login> findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}