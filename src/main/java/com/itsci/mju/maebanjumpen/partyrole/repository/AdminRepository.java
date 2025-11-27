package com.itsci.mju.maebanjumpen.partyrole.repository;

import com.itsci.mju.maebanjumpen.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
}