package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.AccountManager;

import java.util.List;

public interface AccountManagerService {
    List<AccountManager> getAllAccountManagers();
    AccountManager getAccountManagerById(int id);
    AccountManager saveAccountManager(AccountManager accountManager);
    void deleteAccountManager(int id);

    AccountManager updateAccountManager(int id, AccountManager accountManager);
}