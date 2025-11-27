package com.itsci.mju.maebanjumpen.partyrole.repository;

import com.itsci.mju.maebanjumpen.entity.AccountManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountManagerRepository extends JpaRepository<AccountManager, Integer> {
}