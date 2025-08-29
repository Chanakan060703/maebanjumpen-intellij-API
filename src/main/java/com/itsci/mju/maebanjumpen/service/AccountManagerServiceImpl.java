package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.AccountManager;
import com.itsci.mju.maebanjumpen.repository.AccountManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountManagerServiceImpl implements AccountManagerService {

    @Autowired
    private AccountManagerRepository accountManagerRepository;

    @Override
    public List<AccountManager> getAllAccountManagers() {
        return accountManagerRepository.findAll();
    }

    @Override
    public AccountManager getAccountManagerById(int id) {
        return accountManagerRepository.findById(id).orElse(null);
    }

    @Override
    public AccountManager saveAccountManager(AccountManager accountManager) {
        return accountManagerRepository.save(accountManager);
    }

    @Override
    public void deleteAccountManager(int id) {
        AccountManager accountManager = accountManagerRepository.getReferenceById(id);
        accountManagerRepository.delete(accountManager);
    }

    @Override
    public AccountManager updateAccountManager(int id, AccountManager accountManager) {
        AccountManager existingAccountManager = accountManagerRepository.getReferenceById(accountManager.getManagerID());
        if(existingAccountManager == null){
            throw new RuntimeException("Account Manager not found");
        }
        return accountManagerRepository.save(accountManager);
    }
}