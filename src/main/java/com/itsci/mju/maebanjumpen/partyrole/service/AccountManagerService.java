package com.itsci.mju.maebanjumpen.partyrole.service;

import com.itsci.mju.maebanjumpen.partyrole.dto.AccountManagerDTO;

import java.util.List;

public interface AccountManagerService {
    List<AccountManagerDTO> getAllAccountManagers();
    AccountManagerDTO getAccountManagerById(int id);
    AccountManagerDTO saveAccountManager(AccountManagerDTO accountManager);
    void deleteAccountManager(int id);

    AccountManagerDTO updateAccountManager(int id, AccountManagerDTO accountManager);
}