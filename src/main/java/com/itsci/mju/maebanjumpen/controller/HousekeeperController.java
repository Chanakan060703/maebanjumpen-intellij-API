package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.model.Housekeeper;
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

    @GetMapping
    public ResponseEntity<List<Housekeeper>> getAllHousekeepers() {
        List<Housekeeper> housekeepers = housekeeperService.getAllHousekeepers();
        return ResponseEntity.ok(housekeepers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Housekeeper> getHousekeeperById(@PathVariable int id) {
        Housekeeper housekeeper = housekeeperService.getHousekeeperById(id);
        if (housekeeper == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(housekeeper);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Housekeeper>> getHousekeepersByStatus(@PathVariable String status) {
        List<Housekeeper> housekeepers = housekeeperService.getHousekeepersByStatus(status);
        return ResponseEntity.ok(housekeepers);
    }

    // *** เพิ่ม Endpoint ใหม่นี้ ***
    @GetMapping("/unverified-or-null")
    public ResponseEntity<List<Housekeeper>> getUnverifiedOrNullStatusHousekeepers() {
        List<Housekeeper> housekeepers = housekeeperService.getNotVerifiedOrNullStatusHousekeepers();
        return ResponseEntity.ok(housekeepers);
    }

    @PostMapping
    public ResponseEntity<Housekeeper> createHousekeeper(@RequestBody Housekeeper housekeeper) {
        Housekeeper savedHousekeeper = housekeeperService.saveHousekeeper(housekeeper);
        return ResponseEntity.ok(savedHousekeeper);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Housekeeper> updateHousekeeper(@PathVariable int id, @RequestBody Housekeeper housekeeper) {
        Housekeeper updatedHousekeeper = housekeeperService.updateHousekeeper(id, housekeeper);
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
}
