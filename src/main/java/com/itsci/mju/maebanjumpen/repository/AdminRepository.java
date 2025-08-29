package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
}