package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.model.*;
import com.itsci.mju.maebanjumpen.repository.PersonRepository;
import com.itsci.mju.maebanjumpen.service.PartyRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/maeban/party-roles")
public class PartyRoleController {
    @Autowired
    private PartyRoleService partyRoleService;

    @Autowired
    private PersonRepository personRepository;

    @GetMapping
    public ResponseEntity<List<PartyRole>> getAllPartyRole() {
        List<PartyRole> partyRoles = partyRoleService.getAllPartyRoles();
        return ResponseEntity.ok(partyRoles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPartyRole(@PathVariable int id) {
        PartyRole partyRole = partyRoleService.getPartyRoleById(id);
        if (partyRole != null) {
            return ResponseEntity.ok(partyRole);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<?> createPartyRole(@RequestBody Map<String, Object> request) {
        try {
            // ตรวจสอบว่า person_id และ type ไม่เป็น null
            if (!request.containsKey("person_id") || request.get("person_id") == null ||
                    !request.containsKey("type") || request.get("type") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Person ID and type are required."));
            }

            Integer personId = (Integer) request.get("person_id");
            String type = (String) request.get("type");

            // ดึง Person Entity ที่มีอยู่จากฐานข้อมูล
            Optional<Person> personOptional = personRepository.findById(personId);
            if (personOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Person with ID " + personId + " not found."));
            }
            Person person = personOptional.get(); // ได้รับ Person Entity ที่ถูกจัดการโดย JPA

            PartyRole partyRole;
            switch (type.toLowerCase()) {
                // REMOVED: case "member": partyRole = new Member(); break;
                // Reason: Member is an abstract class and cannot be instantiated directly.
                // If you intend for "member" to be a concrete type, remove 'abstract' from Member.java.
                // Otherwise, users should specify a more concrete type like "hirer" or "housekeeper".
                case "hirer":
                    partyRole = new Hirer();
                    break;
                case "housekeeper":
                    partyRole = new Housekeeper();
                    break;
                case "admin":
                    partyRole = new Admin();
                    break;
                case "accountmanager":
                    partyRole = new AccountManager();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid party role type: " + type + ". Please specify a concrete type like 'hirer', 'housekeeper', 'admin', or 'accountmanager'.");
            }

            // ตั้งค่า Person Entity ที่ถูกดึงมาจากฐานข้อมูลให้กับ PartyRole
            partyRole.setPerson(person);

            PartyRole savedPartyRole = partyRoleService.savePartyRole(partyRole);
            return ResponseEntity.ok(savedPartyRole);
        } catch (IllegalArgumentException e) {
            // ดักจับ IllegalArgumentException เพื่อให้ข้อความผิดพลาดที่ชัดเจนขึ้น
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // ดักจับ Exception อื่นๆ ที่ไม่คาดคิด
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePartyRole(@PathVariable int id, @RequestBody PartyRole partyRole) {
        PartyRole updated = partyRoleService.updatePartyRole(id, partyRole);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePartyRole(@PathVariable int id) {
        partyRoleService.deletePartyRole(id);
        return ResponseEntity.noContent().build();
    }
}