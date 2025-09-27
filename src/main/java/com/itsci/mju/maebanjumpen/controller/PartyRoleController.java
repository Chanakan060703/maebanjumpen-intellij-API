// File: src/main/java/com/itsci/mju/maebanjumpen/controller/PartyRoleController.java (No Change)
package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.PartyRoleDTO;
import com.itsci.mju.maebanjumpen.service.PartyRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maeban/party-roles")
@RequiredArgsConstructor
public class PartyRoleController {

    private final PartyRoleService partyRoleService;

    @GetMapping
    public ResponseEntity<List<PartyRoleDTO>> getAllPartyRole() {
        List<PartyRoleDTO> partyRoles = partyRoleService.getAllPartyRoles();
        return ResponseEntity.ok(partyRoles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartyRoleDTO> getPartyRole(@PathVariable int id) {
        PartyRoleDTO partyRole = partyRoleService.getPartyRoleById(id);
        if (partyRole != null) {
            return ResponseEntity.ok(partyRole);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PartyRoleDTO> createPartyRole(@RequestBody PartyRoleDTO partyRoleDto) {
        try {
            PartyRoleDTO savedPartyRole = partyRoleService.savePartyRole(partyRoleDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPartyRole);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartyRoleDTO> updatePartyRole(@PathVariable int id, @RequestBody PartyRoleDTO partyRoleDto) {
        try {
            PartyRoleDTO updated = partyRoleService.updatePartyRole(id, partyRoleDto);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartyRole(@PathVariable int id) {
        partyRoleService.deletePartyRole(id);
        return ResponseEntity.noContent().build();
    }
}