package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.AccountManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountManagerRepository extends JpaRepository<AccountManager, Integer> {
}