package com.itsci.mju.maebanjumpen.partyrole.controller;

import com.itsci.mju.maebanjumpen.partyrole.dto.AccountManagerDTO;
import com.itsci.mju.maebanjumpen.partyrole.service.AccountManagerService;
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
    public ResponseEntity<List<AccountManagerDTO>> getAllAccountManagers() {
        List<AccountManagerDTO> accountManagers = accountManagerService.getAllAccountManagers();
        return ResponseEntity.ok(accountManagers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountManagerDTO> getAccountManagerById(@PathVariable int id) {
        AccountManagerDTO accountManager = accountManagerService.getAccountManagerById(id);
        return ResponseEntity.ok(accountManager);
    }

    @PostMapping
    public ResponseEntity<AccountManagerDTO> createAccountManager(@RequestBody AccountManagerDTO accountManager) {
        AccountManagerDTO savedAccountManager = accountManagerService.saveAccountManager(accountManager);
        return ResponseEntity.ok(savedAccountManager);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountManagerDTO> updateAccountManager(@PathVariable int id, @RequestBody AccountManagerDTO accountManager) {
        AccountManagerDTO updatedAccountManager = accountManagerService.updateAccountManager(id, accountManager);
        return ResponseEntity.ok(updatedAccountManager);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountManager(@PathVariable int id) {
        accountManagerService.deleteAccountManager(id);
        return ResponseEntity.noContent().build();
    }
}