package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.HousekeeperDTO;
import com.itsci.mju.maebanjumpen.dto.HousekeeperDetailDTO; // 💡 เพิ่ม import DTO ตัวใหม่
import com.itsci.mju.maebanjumpen.service.HousekeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maeban/housekeepers")
public class HousekeeperController {

    @Autowired
    private HousekeeperService housekeeperService;

    // ... (เมธอด getAllHousekeepers เหมือนเดิม)
    @GetMapping
    public ResponseEntity<List<HousekeeperDTO>> getAllHousekeepers() {
        List<HousekeeperDTO> housekeepers = housekeeperService.getAllHousekeepers();
        return ResponseEntity.ok(housekeepers);
    }

    // 🎯 การแก้ไข 1: เปลี่ยนชื่อ Endpoint และชนิดข้อมูลที่ส่งคืน
    // Endpoint นี้ใช้สำหรับดึง "รายละเอียด" ที่มี JobsCompleted และ Reviews
    @GetMapping("/{id}")
    public ResponseEntity<HousekeeperDetailDTO> getHousekeeperDetailById(@PathVariable int id) {
        // ใช้เมธอด getHousekeeperDetailById จาก Service
        HousekeeperDetailDTO housekeeper = housekeeperService.getHousekeeperDetailById(id);
        if (housekeeper == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(housekeeper);
    }

    // ... (เมธอด getHousekeepersByStatus เหมือนเดิม)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<HousekeeperDTO>> getHousekeepersByStatus(@PathVariable String status) {
        List<HousekeeperDTO> housekeepers = housekeeperService.getHousekeepersByStatus(status);
        return ResponseEntity.ok(housekeepers);
    }

    // ... (เมธอด getUnverifiedOrNullStatusHousekeepers เหมือนเดิม)
    @GetMapping("/unverified-or-null")
    public ResponseEntity<List<HousekeeperDTO>> getUnverifiedOrNullStatusHousekeepers() {
        List<HousekeeperDTO> housekeepers = housekeeperService.getNotVerifiedOrNullStatusHousekeepers();
        return ResponseEntity.ok(housekeepers);
    }

    // ... (เมธอด CRUD อื่นๆ เหมือนเดิม)
    @PostMapping
    public ResponseEntity<HousekeeperDTO> createHousekeeper(@RequestBody HousekeeperDTO housekeeper) {
        HousekeeperDTO savedHousekeeper = housekeeperService.saveHousekeeper(housekeeper);
        return ResponseEntity.ok(savedHousekeeper);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HousekeeperDTO> updateHousekeeper(@PathVariable int id, @RequestBody HousekeeperDTO housekeeper) {
        HousekeeperDTO updatedHousekeeper = housekeeperService.updateHousekeeper(id, housekeeper);
        if (updatedHousekeeper == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedHousekeeper);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHousekeeper(@PathVariable int id) {
        housekeeperService.deleteHousekeeper(id);
        return ResponseEntity.noContent().build();
    }

    // 💡 หากต้องการแยก Endpoint ดึง HousekeeperDTO (ข้อมูลพื้นฐาน)
    // ควรสร้างเมธอดใหม่ เช่น getHousekeeperBaseById และเพิ่มใน Service
    // แต่สำหรับตอนนี้ การใช้ getHousekeeperDetailById ใน /housekeepers/{id} ก็เพียงพอแล้ว
}