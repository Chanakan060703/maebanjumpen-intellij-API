package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.mapper.AdminMapper
import com.itsci.mju.maebanjumpen.partyrole.dto.AdminDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.AdminRepository
import com.itsci.mju.maebanjumpen.partyrole.service.AdminService
import org.springframework.stereotype.Service

@Service
class AdminServiceImpl(
    private val adminMapper: AdminMapper,
    private val adminRepository: AdminRepository
) : AdminService {

    override fun getAllAdmins(): List<AdminDTO> {
        val entities = adminRepository.findAll()
        return adminMapper.toDtoList(entities)
    }

    override fun getAdminById(id: Int): AdminDTO {
        val entity = adminRepository.findById(id)
            .orElseThrow { NoSuchElementException("Admin not found with ID: $id") }
        return adminMapper.toDto(entity)
    }

    override fun saveAdmin(adminDto: AdminDTO): AdminDTO {
        val entityToSave = adminMapper.toEntity(adminDto)
        val savedEntity = adminRepository.save(entityToSave)
        return adminMapper.toDto(savedEntity)
    }

    override fun deleteAdmin(id: Int) {
        if (!adminRepository.existsById(id)) {
            throw NoSuchElementException("Admin not found with ID: $id")
        }
        adminRepository.deleteById(id)
    }

    override fun updateAdmin(id: Int, adminDto: AdminDTO): AdminDTO {
        val existingAdmin = adminRepository.findById(id)
            .orElseThrow { NoSuchElementException("Admin not found with ID: $id") }

        existingAdmin.adminStatus = adminDto.adminStatus
        val updatedEntity = adminRepository.save(existingAdmin)
        return adminMapper.toDto(updatedEntity)
    }
}

