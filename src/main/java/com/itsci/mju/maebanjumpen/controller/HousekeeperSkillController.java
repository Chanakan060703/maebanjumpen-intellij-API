package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.HousekeeperSkill;
import com.itsci.mju.maebanjumpen.model.SkillType;
import com.itsci.mju.maebanjumpen.repository.HousekeeperRepository; // ต้องมี Repository นี้
import com.itsci.mju.maebanjumpen.repository.SkillTypeRepository;     // ต้องมี Repository นี้
import com.itsci.mju.maebanjumpen.service.HousekeeperSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Import Map
import java.util.Optional; // Import Optional

@RestController
@RequestMapping("/maeban/housekeeper-skills")
public class HousekeeperSkillController {

    @Autowired
    private HousekeeperSkillService housekeeperSkillService;

    // ต้องเพิ่ม Repository สำหรับ Housekeeper และ SkillType
    @Autowired
    private HousekeeperRepository housekeeperRepository;
    @Autowired
    private SkillTypeRepository skillTypeRepository;

    @GetMapping
    public ResponseEntity<List<HousekeeperSkill>> getAllHousekeeperSkills() {
        List<HousekeeperSkill> skills = housekeeperSkillService.getAllHousekeeperSkills();
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HousekeeperSkill> getHousekeeperSkillById(@PathVariable int id) {
        HousekeeperSkill skill = housekeeperSkillService.getHousekeeperSkillById(id);
        return ResponseEntity.ok(skill);
    }

    @PostMapping // เปลี่ยนวิธีการรับ Request Body
    public ResponseEntity<?> createHousekeeperSkill(@RequestBody Map<String, Integer> payload) {
        try {
            Integer housekeeperId = payload.get("housekeeperId"); // อ่าน housekeeperId จาก payload
            Integer skillTypeId = payload.get("skillTypeId");     // อ่าน skillTypeId จาก payload

            if (housekeeperId == null || skillTypeId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Housekeeper ID and SkillType ID are required."));
            }

            // ดึง Housekeeper entity เต็มๆ จาก ID
            Optional<Housekeeper> housekeeperOptional = housekeeperRepository.findById(housekeeperId);
            if (housekeeperOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Housekeeper not found with ID: " + housekeeperId));
            }
            Housekeeper housekeeper = housekeeperOptional.get();

            // ดึง SkillType entity เต็มๆ จาก ID
            Optional<SkillType> skillTypeOptional = skillTypeRepository.findById(skillTypeId);
            if (skillTypeOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "SkillType not found with ID: " + skillTypeId));
            }
            SkillType skillType = skillTypeOptional.get();

            // สร้าง HousekeeperSkill object ใหม่
            HousekeeperSkill newHousekeeperSkill = new HousekeeperSkill();
            newHousekeeperSkill.setHousekeeper(housekeeper);
            newHousekeeperSkill.setSkillType(skillType);
            // กำหนดค่าเริ่มต้นสำหรับ skillLevel
            // เนื่องจากในโมเดล HousekeeperSkill ของคุณ skillLevel เป็น nullable = false
            // คุณต้องกำหนดค่าให้มันที่นี่ ถ้า Flutter ไม่ได้ส่งมา
            newHousekeeperSkill.setSkillLevel("Beginner"); // หรือค่าเริ่มต้นอื่นๆ ที่เหมาะสม เช่น "Basic"

            HousekeeperSkill savedSkill = housekeeperSkillService.saveHousekeeperSkill(newHousekeeperSkill);
            return ResponseEntity.ok(savedSkill);
        } catch (Exception e) {
            e.printStackTrace(); // พิมพ์ stack trace เพื่อดูข้อผิดพลาดแบบละเอียด
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to add skill: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HousekeeperSkill> updateHousekeeperSkill(@PathVariable int id, @RequestBody HousekeeperSkill skill) {
        // ต้องปรับ Logic ตรงนี้ด้วย หากคุณต้องการอัปเดต housekeeper/skillType ID ผ่าน PUT
        // หรือถ้าแค่ต้องการอัปเดต skillLevel
        HousekeeperSkill existingHousekeeperSkill = housekeeperSkillService.getHousekeeperSkillById(id);
        if (existingHousekeeperSkill == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // อัปเดตเฉพาะ field ที่อนุญาตให้อัปเดต
        existingHousekeeperSkill.setSkillLevel(skill.getSkillLevel()); // ถ้าต้องการอัปเดต skillLevel
        // ไม่ควรให้อัปเดต housekeeper หรือ skillType ผ่าน PUT นี้ ถ้าไม่ได้มี logic ที่ชัดเจน

        HousekeeperSkill updatedSkill = housekeeperSkillService.saveHousekeeperSkill(existingHousekeeperSkill);
        return ResponseEntity.ok(updatedSkill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHousekeeperSkill(@PathVariable int id) { // เปลี่ยน return type
        try {
            // ตรวจสอบว่ามี skillId นั้นอยู่จริงหรือไม่ก่อนลบ
            if (housekeeperSkillService.getHousekeeperSkillById(id) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", "HousekeeperSkill not found with ID: " + id));
            }
            housekeeperSkillService.deleteHousekeeperSkill(id);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Skill deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message", "Failed to delete skill: " + e.getMessage()));
        }
    }
}