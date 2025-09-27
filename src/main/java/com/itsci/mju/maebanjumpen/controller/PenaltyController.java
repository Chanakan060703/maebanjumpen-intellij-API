package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.PenaltyDTO;
import com.itsci.mju.maebanjumpen.model.Penalty;
import com.itsci.mju.maebanjumpen.service.PenaltyService;
import com.itsci.mju.maebanjumpen.service.ReportService; // นำเข้า ReportService
import com.itsci.mju.maebanjumpen.service.PersonService; // นำเข้า PersonService
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maeban/penalties")
@RequiredArgsConstructor // ใช้ Constructor Injection แทน @Autowired
public class PenaltyController {

    private final PenaltyService penaltyService;
    // ⚠️ ลบ Repositories และ Services ที่ไม่จำเป็นสำหรับ Controller ออก
    // private final ReportService reportService;
    // private final PersonService personService;
    // private final HirerRepository hirerRepository;
    // private final HousekeeperRepository housekeeperRepository;

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
    public ResponseEntity<PenaltyDTO> createPenalty(@RequestBody PenaltyDTO penaltyDto) {
        try {
            PenaltyDTO savedPenalty = penaltyService.savePenalty(penaltyDto);
            // 💡 ตรรกะการอัปเดตสถานะบัญชีและการอัปเดต Report ถูกย้ายไปอยู่ใน PenaltyService.savePenalty แล้ว
            // Controller มีหน้าที่แค่รับ Request และเรียกใช้ Service เท่านั้น
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
            PenaltyDTO updatedPenalty = penaltyService.updatePenalty(id, penaltyDto);
            // 💡 ตรรกะการอัปเดตสถานะบัญชีถูกย้ายไปอยู่ใน PenaltyService.updatePenalty แล้ว
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
        penaltyService.deletePenalty(id);
        return ResponseEntity.noContent().build();
    }
}