package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.PenaltyDTO;
import com.itsci.mju.maebanjumpen.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maeban/penalties")
@RequiredArgsConstructor // ใช้ Constructor Injection
public class PenaltyController {

    private final PenaltyService penaltyService;

    @GetMapping
    public ResponseEntity<List<PenaltyDTO>> getAllPenalties() {
        List<PenaltyDTO> penalties = penaltyService.getAllPenalties();
        return ResponseEntity.ok(penalties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PenaltyDTO> getPenaltyById(@PathVariable int id) {
        PenaltyDTO penalty = penaltyService.getPenaltyById(id);
        if (penalty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(penalty);
    }

    @PostMapping
    public ResponseEntity<PenaltyDTO> createPenalty(
            @RequestBody PenaltyDTO penaltyDto,
            // ⬅️ เพิ่ม reportId เป็น Query Parameter เพื่อแก้ไขปัญหาการขาดหายของ ID ใน DTO
            @RequestParam int reportId,
            // เพิ่ม housekeeperId เป็น optional Query Parameter ตามที่เห็นใน Log (แม้จะไม่ได้ใช้โดยตรงใน Service)
            @RequestParam(required = false) Integer housekeeperId
    ) {
        try {
            // ⬅️ แก้ไข: กำหนดค่า reportId ที่รับจาก URL ให้กับ DTO ก่อนส่งเข้า Service
            penaltyDto.setReportId(reportId);

            // เรียกใช้ Service เพื่อสร้าง Penalty, ผูกกับ Report และอัปเดตสถานะบัญชี
            PenaltyDTO savedPenalty = penaltyService.savePenalty(penaltyDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPenalty);
        } catch (Exception e) {
            System.err.println("Error creating penalty: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<PenaltyDTO> updatePenalty(@PathVariable int id, @RequestBody PenaltyDTO penaltyDto) {
        try {
            // เรียกใช้ Service เพื่ออัปเดต Penalty และอัปเดตสถานะบัญชีหากจำเป็น
            PenaltyDTO updatedPenalty = penaltyService.updatePenalty(id, penaltyDto);
            return ResponseEntity.ok(updatedPenalty);
        } catch (RuntimeException e) {
            System.err.println("Error updating penalty: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Error during penalty update: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePenalty(@PathVariable int id) {
        // Service จะจัดการ unlink จาก Report ก่อนลบ
        penaltyService.deletePenalty(id);
        return ResponseEntity.noContent().build();
    }
}
