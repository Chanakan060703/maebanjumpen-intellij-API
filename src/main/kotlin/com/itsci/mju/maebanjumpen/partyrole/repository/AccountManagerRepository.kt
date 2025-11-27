package com.itsci.mju.maebanjumpen.partyrole.repository

import com.itsci.mju.maebanjumpen.entity.AccountManager
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountManagerRepository : JpaRepository<AccountManager, Int>

