package com.itsci.mju.maebanjumpen.penalty.controller;

import com.itsci.mju.maebanjumpen.penalty.dto.PenaltyDTO;
import com.itsci.mju.maebanjumpen.penalty.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maeban/penalties")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;

    /**
     * ✅ **FIXED:** สร้างบทลงโทษใหม่และเชื่อมโยงกับ Report รวมถึงอัปเดตสถานะบัญชีของผู้ถูกลงโทษ
     * 🎯 ใช้วิธีรับ ID เป้าหมาย (hirerId หรือ housekeeperId) เพื่อระบุตัวผู้ถูกลงโทษอย่างแม่นยำ
     * * Request Example: POST /maeban/penalties?reportId=25&hirerId=3
     */
    @PostMapping
    public ResponseEntity<PenaltyDTO> createPenalty(
            @RequestBody PenaltyDTO penaltyDTO,
            @RequestParam("reportId") int reportId,
            @RequestParam(value = "hirerId", required = false) Integer hirerId, // Role ID ของ Hirer (Target)
            @RequestParam(value = "housekeeperId", required = false) Integer housekeeperId // Role ID ของ Housekeeper (Target)
    ) {

        // 1. กำหนด ID ของบทบาทที่เป็นเป้าหมาย (Target Role ID)
        Integer targetRoleId = null;
        if (hirerId != null) {
            // กรณีลงโทษ Hirer (Log ก่อนหน้าคือ Hirer Role ID 3)
            targetRoleId = hirerId;
        } else if (housekeeperId != null) {
            // กรณีลงโทษ Housekeeper
            targetRoleId = housekeeperId;
        } else {
            // หากไม่มี Role ID เป้าหมายถูกระบุมา ถือว่าเป็น Bad Request
            System.err.println("Error: Missing target Role ID (hirerId or housekeeperId) for penalty creation.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // 2. ตั้งค่า reportId ใน DTO
        penaltyDTO.setReportId(reportId);

        try {
            // 3. 🎯 เรียก Service เมธอดใหม่ที่รับ targetRoleId
            PenaltyDTO createdPenalty = penaltyService.savePenalty(penaltyDTO, targetRoleId);

            // เมื่อสร้างสำเร็จ จะส่ง HTTP 201 Created
            return new ResponseEntity<>(createdPenalty, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating penalty: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Unexpected error creating penalty: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- CRUD Methods for Penalty ---

    @GetMapping
    public ResponseEntity<List<PenaltyDTO>> getAllPenalties() {
        List<PenaltyDTO> penalties = penaltyService.getAllPenalties();
        return new ResponseEntity<>(penalties, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PenaltyDTO> getPenaltyById(@PathVariable int id) {
        PenaltyDTO penalty = penaltyService.getPenaltyById(id);
        if (penalty != null) {
            return new ResponseEntity<>(penalty, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PenaltyDTO> updatePenalty(@PathVariable int id, @RequestBody PenaltyDTO penaltyDTO) {
        try {
            // NOTE: การอัปเดตบทลงโทษควรมีการส่ง Role ID ของผู้ที่ถูกลงโทษมาด้วย
            // หากต้องการให้มีการอัปเดตสถานะบัญชีอย่างปลอดภัยหลังจากการเปลี่ยน PenaltyType
            PenaltyDTO updatedPenalty = penaltyService.updatePenalty(id, penaltyDTO);
            return new ResponseEntity<>(updatedPenalty, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePenalty(@PathVariable int id) {
        try {
            penaltyService.deletePenalty(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
