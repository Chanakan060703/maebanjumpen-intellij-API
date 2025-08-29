package com.itsci.mju.maebanjumpen.controller;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException;
import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException;
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.service.HireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/maeban/hires")
public class HireController {

    @Autowired
    private HireService hireService;

    @GetMapping
    public ResponseEntity<List<Hire>> getAllHires() {
        try {
            List<Hire> hires = hireService.getAllHires();
            return ResponseEntity.ok(hires);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHireById(@PathVariable String id) {
        try {
            int hireId = Integer.parseInt(id);
            Hire hire = hireService.getHireById(hireId);
            return hire != null ? ResponseEntity.ok(hire) : ResponseEntity.notFound().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid hireId format"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createHire(@RequestBody Hire hire) {
        System.out.println("Received Hire for creation: " + hire);
        if (hire.getHirer() != null) {
            System.out.println("Hirer ID: " + hire.getHirer().getId());
        }
        if (hire.getHousekeeper() != null) {
            System.out.println("Housekeeper ID: " + hire.getHousekeeper().getId());
        }
        if (hire.getHireDetail() == null || hire.getHireDetail().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "hireDetail is required"));
        }

        try {
            Hire savedHire = hireService.saveHire(hire);
            return new ResponseEntity<>(savedHire, HttpStatus.CREATED); // ส่งคืน 201 Created
        } catch (HirerNotFoundException | HousekeeperNotFoundException e) {
            System.err.println("Error creating hire: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating hire: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error creating hire: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("/{hireId}")
    public ResponseEntity<?> updateHire(@PathVariable int hireId, @RequestBody Hire hireDetailsFromClient) {
        try {
            Hire updatedHire = hireService.updateHire(hireId, hireDetailsFromClient);

            if (updatedHire != null) {
                return ResponseEntity.ok(updatedHire);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InsufficientBalanceException e) {
            // หากเงินไม่พอของผู้จ้าง
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (HirerNotFoundException | HousekeeperNotFoundException e) { // เพิ่ม HousekeeperNotFoundException ที่นี่
            // หากไม่พบผู้จ้างหรือแม่บ้าน
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            // สำหรับข้อผิดพลาดเกี่ยวกับข้อมูลที่หายไป เช่น hirerId, housekeeperId, หรือ paymentAmount เป็น null
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // เพิ่ม endpoint ใหม่สำหรับเพิ่มรูปภาพความคืบหน้า
    @PatchMapping(path = "/{hireId}/add-progression-images", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProgressionImagesToHire(@PathVariable int hireId, @RequestBody List<String> imageUrls) {
        try {
            System.out.println("Received request to add progression images for hireId: " + hireId);
            System.out.println("Image URLs: " + imageUrls);
            Hire updatedHire = hireService.addProgressionImagesToHire(hireId, imageUrls);
            return ResponseEntity.ok(updatedHire);
        } catch (IllegalArgumentException e) {
            System.err.println("Error adding progression images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error adding progression images: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHire(@PathVariable int id) {
        try {
            hireService.deleteHire(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/hirer/{hirerId}")
    public ResponseEntity<?> getHiresByHirerId(@PathVariable int hirerId) {
        try {
            List<Hire> hires = hireService.getHiresByHirerId(hirerId);
            return ResponseEntity.ok(hires);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/housekeepers/{housekeeperId}")
    public ResponseEntity<?> getHiresByHousekeeperId(@PathVariable int housekeeperId) {
        try {
            List<Hire> hires = hireService.getHiresByHousekeeperId(housekeeperId);
            return ResponseEntity.ok(hires); // ตรงนี้คือที่ Jackson ทำการ Serialize
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}