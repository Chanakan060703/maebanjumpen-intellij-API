package com.itsci.mju.maebanjumpen.service.impl;

import com.itsci.mju.maebanjumpen.dto.AccountManagerDTO;
import com.itsci.mju.maebanjumpen.mapper.AccountManagerMapper; // ⬅️ ต้องมี Mapper
import com.itsci.mju.maebanjumpen.model.AccountManager; // ⬅️ ต้องใช้ Entity
import com.itsci.mju.maebanjumpen.repository.AccountManagerRepository;
import com.itsci.mju.maebanjumpen.service.AccountManagerService;
import lombok.RequiredArgsConstructor; // ⬅️ ใช้แทน @Autowired
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor // Inject final fields
public class AccountManagerServiceImpl implements AccountManagerService {

    private final AccountManagerRepository accountManagerRepository;
    private final AccountManagerMapper accountManagerMapper; // ⬅️ Inject Mapper

    @Override
    public List<AccountManagerDTO> getAllAccountManagers() {
        List<AccountManager> entities = accountManagerRepository.findAll();

        return accountManagerMapper.toDtoList(entities);
    }

    @Override
    public AccountManagerDTO getAccountManagerById(int id) {
        AccountManager entity = accountManagerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account Manager not found with ID: " + id));

        return accountManagerMapper.toDto(entity);
    }

    @Override
    public AccountManagerDTO saveAccountManager(AccountManagerDTO accountManagerDto) {
        AccountManager entity = accountManagerMapper.toEntity(accountManagerDto);
        AccountManager savedEntity = accountManagerRepository.save(entity);
        return accountManagerMapper.toDto(savedEntity);
    }

    @Override
    public AccountManagerDTO updateAccountManager(int id, AccountManagerDTO accountManagerDto) {
        AccountManager existingAccountManager = accountManagerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account Manager not found with ID: " + id));

        existingAccountManager.setManagerID(accountManagerDto.getManagerID());
        AccountManager updatedEntity = accountManagerRepository.save(existingAccountManager);
        return accountManagerMapper.toDto(updatedEntity);
    }

    @Override
    public void deleteAccountManager(int id) {
        if (!accountManagerRepository.existsById(id)) {
            throw new NoSuchElementException("Account Manager not found with ID: " + id);
        }
        accountManagerRepository.deleteById(id);
    }
}