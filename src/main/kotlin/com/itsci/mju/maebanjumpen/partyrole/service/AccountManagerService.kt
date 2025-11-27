package com.itsci.mju.maebanjumpen.partyrole.service

import com.itsci.mju.maebanjumpen.partyrole.dto.AccountManagerDTO

interface AccountManagerService {
    fun listAllAccountManagers(): List<AccountManagerDTO>
    fun getAccountManagerById(id: Int): AccountManagerDTO
    fun createAccountManager(accountManager: AccountManagerDTO): AccountManagerDTO
    fun deleteAccountManager(id: Int)
    fun updateAccountManager(id: Int, accountManager: AccountManagerDTO): AccountManagerDTO
}

