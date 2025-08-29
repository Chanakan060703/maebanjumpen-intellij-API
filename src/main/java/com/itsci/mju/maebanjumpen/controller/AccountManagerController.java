package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.model.AccountManager;
import com.itsci.mju.maebanjumpen.service.AccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maeban/account-managers")
public class AccountManagerController {

    @Autowired
    private AccountManagerService accountManagerService;

    @GetMapping
    public ResponseEntity<List<AccountManager>> getAllAccountManagers() {
        List<AccountManager> accountManagers = accountManagerService.getAllAccountManagers();
        return ResponseEntity.ok(accountManagers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountManager> getAccountManagerById(@PathVariable int id) {
        AccountManager accountManager = accountManagerService.getAccountManagerById(id);
        return ResponseEntity.ok(accountManager);
    }

    @PostMapping
    public ResponseEntity<AccountManager> createAccountManager(@RequestBody AccountManager accountManager) {
        AccountManager savedAccountManager = accountManagerService.saveAccountManager(accountManager);
        return ResponseEntity.ok(savedAccountManager);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountManager> updateAccountManager(@PathVariable int id, @RequestBody AccountManager accountManager) {
        AccountManager updatedAccountManager = accountManagerService.updateAccountManager(id, accountManager);
        return ResponseEntity.ok(updatedAccountManager);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountManager(@PathVariable int id) {
        accountManagerService.deleteAccountManager(id);
        return ResponseEntity.noContent().build();
    }
}