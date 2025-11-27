package com.itsci.mju.maebanjumpen.housekeeperskill.controller;

import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO; // สำหรับเมธอด getAllHousekeeperSkills
import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO;
import com.itsci.mju.maebanjumpen.housekeeperskill.service.HousekeeperSkillService;
import lombok.RequiredArgsConstructor; // ⬅️ ใช้สำหรับ DI
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/maeban/housekeeper-skills")
@RequiredArgsConstructor // ⬅️ ใช้แทน @Autowired
public class HousekeeperSkillController {

    private final HousekeeperSkillService housekeeperSkillService;

    // 1.getAllHousekeeperSkills: คืนค่า List<HousekeeperDTO> ตาม Interface Service
    @GetMapping
    public ResponseEntity<List<HousekeeperDTO>> getAllHousekeeperSkills() {
        // Service Layer จัดการการดึง Entity และแปลงเป็น DTO แล้ว
        List<HousekeeperDTO> skills = housekeeperSkillService.getAllHousekeeperSkills();
        return ResponseEntity.ok(skills);
    }

    // 2. getHousekeeperSkillById: คืนค่า HousekeeperSkillDTO
    @GetMapping("/{id}")
    public ResponseEntity<HousekeeperSkillDTO> getHousekeeperSkillById(@PathVariable int id) {
        HousekeeperSkillDTO skill = housekeeperSkillService.getHousekeeperSkillById(id);
        if (skill == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skill);
    }

    // --- POST method (Receive and Return DTO) ---

    // 3. createHousekeeperSkill: รับ HousekeeperSkillDTO และคืนค่า DTO ที่ถูกสร้าง
    @PostMapping
    public ResponseEntity<HousekeeperSkillDTO> createHousekeeperSkill(@RequestBody HousekeeperSkillDTO housekeeperSkillDto) {
        try {
            // Service Layer จะจัดการตรรกะทั้งหมด: การตรวจสอบ ID, การหา Entity, และการสร้าง Entity
            HousekeeperSkillDTO createdSkill = housekeeperSkillService.saveHousekeeperSkill(housekeeperSkillDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSkill);
        } catch (IllegalArgumentException e) {
            // มักเกิดจากการให้ ID ไม่ครบ หรือ Entity ที่อ้างอิงไม่พบ (Service ควรโยน Exception ชนิดนี้)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            // เช่น Conflict หรือ IllegalStateException จาก Service Layer
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- PUT method (Receive and Return DTO) ---

    // 4. updateHousekeeperSkill: รับ HousekeeperSkillDTO และคืนค่า DTO ที่อัปเดต
    @PutMapping("/{id}")
    public ResponseEntity<HousekeeperSkillDTO> updateHousekeeperSkill(@PathVariable int id, @RequestBody HousekeeperSkillDTO skillDto) {
        try {
            // Service Layer จัดการตรรกะการอัปเดตทั้งหมด
            HousekeeperSkillDTO updatedSkill = housekeeperSkillService.updateHousekeeperSkill(id, skillDto);
            return ResponseEntity.ok(updatedSkill);
        } catch (RuntimeException e) {
            // รวมถึง RuntimeException จาก Service
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- DELETE method ---

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHousekeeperSkill(@PathVariable int id) {
        try {
            // ตรวจสอบสถานะการมีอยู่จาก Service
            if (housekeeperSkillService.getHousekeeperSkillById(id) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", "HousekeeperSkill not found with ID: " + id));
            }
            housekeeperSkillService.deleteHousekeeperSkill(id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Skill deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message", "Failed to delete skill: " + e.getMessage()));
        }
    }
}