package com.itsci.mju.maebanjumpen.partyrole.controller

import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO
import com.itsci.mju.maebanjumpen.partyrole.service.PartyRoleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/party-roles")
class PartyRoleController(private val partyRoleService: PartyRoleService) {

    @GetMapping
    fun getAllPartyRole(): ResponseEntity<List<PartyRoleDTO>> {
        val partyRoles = partyRoleService.getAllPartyRoles()
        return ResponseEntity.ok(partyRoles)
    }

    @GetMapping("/{id}")
    fun getPartyRole(@PathVariable id: Int): ResponseEntity<PartyRoleDTO> {
        val partyRole = partyRoleService.getPartyRoleById(id)
        return if (partyRole != null) {
            ResponseEntity.ok(partyRole)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createPartyRole(@RequestBody partyRoleDto: PartyRoleDTO): ResponseEntity<PartyRoleDTO?> {
        return try {
            val savedPartyRole = partyRoleService.savePartyRole(partyRoleDto)
            ResponseEntity.status(HttpStatus.CREATED).body(savedPartyRole)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @PutMapping("/{id}")
    fun updatePartyRole(@PathVariable id: Int, @RequestBody partyRoleDto: PartyRoleDTO): ResponseEntity<PartyRoleDTO?> {
        return try {
            val updated = partyRoleService.updatePartyRole(id, partyRoleDto)
            if (updated != null) {
                ResponseEntity.ok(updated)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @DeleteMapping("/{id}")
    fun deletePartyRole(@PathVariable id: Int): ResponseEntity<Void> {
        partyRoleService.deletePartyRole(id)
        return ResponseEntity.noContent().build()
    }
}

