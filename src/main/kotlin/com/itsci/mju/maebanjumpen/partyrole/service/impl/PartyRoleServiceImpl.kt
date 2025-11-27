package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.mapper.PartyRoleMapper
import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository
import com.itsci.mju.maebanjumpen.partyrole.service.PartyRoleService
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PartyRoleServiceImpl(
    private val partyRoleRepository: PartyRoleRepository,
    private val partyRoleMapper: PartyRoleMapper,
    private val personRepository: PersonRepository
) : PartyRoleService {

    @Transactional
    override fun savePartyRole(partyRoleDto: PartyRoleDTO): PartyRoleDTO {
        if (partyRoleDto.person?.personId == null) {
            throw IllegalArgumentException("Person ID is required to create a PartyRole.")
        }

        val personId = partyRoleDto.person!!.personId!!
        val existingPerson = personRepository.findById(personId)
            .orElseThrow { RuntimeException("Person ID: $personId not found. Failed to link PartyRole.") }

        val partyRole = partyRoleMapper.toEntity(partyRoleDto)
            ?: throw IllegalArgumentException("Failed to convert PartyRoleDTO to entity.")
        partyRole.person = existingPerson

        val savedPartyRole = partyRoleRepository.save(partyRole)
        return partyRoleMapper.toDto(savedPartyRole)
            ?: throw IllegalStateException("Failed to convert saved PartyRole to DTO.")
    }

    @Transactional(readOnly = true)
    override fun getPartyRoleById(id: Int): PartyRoleDTO? {
        return partyRoleRepository.findById(id)
            .map { partyRoleMapper.toDto(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun getAllPartyRoles(): List<PartyRoleDTO> {
        val partyRoles = partyRoleRepository.findAll()
        return partyRoleMapper.toDtoList(partyRoles)
    }

    @Transactional
    override fun updatePartyRole(id: Int, partyRoleDto: PartyRoleDTO): PartyRoleDTO {
        return partyRoleRepository.findById(id).map { existingPartyRole ->
            val updatedDetails = partyRoleMapper.toEntity(partyRoleDto)

            if (existingPartyRole.person != null && updatedDetails?.person != null) {
                // Update logic can be added here
            }

            val savedRole = partyRoleRepository.save(existingPartyRole)
            partyRoleMapper.toDto(savedRole)
                ?: throw IllegalStateException("Failed to convert saved PartyRole to DTO.")
        }.orElseThrow { RuntimeException("PartyRole not found with ID: $id") }
    }

    @Transactional
    override fun deletePartyRole(id: Int) {
        partyRoleRepository.deleteById(id)
    }
}

