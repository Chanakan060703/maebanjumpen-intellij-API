package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.mapper.AccountManagerMapper
import com.itsci.mju.maebanjumpen.partyrole.dto.AccountManagerDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.AccountManagerRepository
import com.itsci.mju.maebanjumpen.partyrole.service.AccountManagerService
import org.springframework.stereotype.Service

@Service
class AccountManagerServiceImpl(
    private val accountManagerRepository: AccountManagerRepository,
    private val accountManagerMapper: AccountManagerMapper
) : AccountManagerService {

    override fun getAllAccountManagers(): List<AccountManagerDTO> {
        val entities = accountManagerRepository.findAll()
        return accountManagerMapper.toDtoList(entities)
    }

    override fun getAccountManagerById(id: Int): AccountManagerDTO {
        val entity = accountManagerRepository.findById(id)
            .orElseThrow { NoSuchElementException("Account Manager not found with ID: $id") }
        return accountManagerMapper.toDto(entity)
    }

    override fun saveAccountManager(accountManagerDto: AccountManagerDTO): AccountManagerDTO {
        val entity = accountManagerMapper.toEntity(accountManagerDto)
        val savedEntity = accountManagerRepository.save(entity)
        return accountManagerMapper.toDto(savedEntity)
    }

    override fun updateAccountManager(id: Int, accountManagerDto: AccountManagerDTO): AccountManagerDTO {
        val existingAccountManager = accountManagerRepository.findById(id)
            .orElseThrow { NoSuchElementException("Account Manager not found with ID: $id") }

        existingAccountManager.managerID = accountManagerDto.managerID
        val updatedEntity = accountManagerRepository.save(existingAccountManager)
        return accountManagerMapper.toDto(updatedEntity)
    }

    override fun deleteAccountManager(id: Int) {
        if (!accountManagerRepository.existsById(id)) {
            throw NoSuchElementException("Account Manager not found with ID: $id")
        }
        accountManagerRepository.deleteById(id)
    }
}

