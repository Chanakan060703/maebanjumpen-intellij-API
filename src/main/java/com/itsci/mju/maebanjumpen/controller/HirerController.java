package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.HirerDTO;
import com.itsci.mju.maebanjumpen.model.Hirer;
import com.itsci.mju.maebanjumpen.service.HirerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maeban/hirers")
public class HirerController {

    @Autowired
    private HirerService hirerService;

    @GetMapping
    public ResponseEntity<List<HirerDTO>> getAllHirers() {
        List<HirerDTO> hirers = hirerService.getAllHirers();
        return ResponseEntity.ok(hirers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HirerDTO> getHirerById(@PathVariable int id) {
        HirerDTO hirer = hirerService.getHirerById(id);
        return ResponseEntity.ok(hirer);
    }

    @PostMapping
    public ResponseEntity<HirerDTO> createHirer(@RequestBody HirerDTO hirer) {
        HirerDTO savedHirer = hirerService.saveHirer(hirer);
        return ResponseEntity.ok(savedHirer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HirerDTO> updateHirer(@PathVariable int id, @RequestBody HirerDTO hirer) {
        HirerDTO updatedHirer = hirerService.updateHirer(id, hirer);
        return ResponseEntity.ok(updatedHirer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHirer(@PathVariable int id) {
        hirerService.deleteHirer(id);
        return ResponseEntity.noContent().build();
    }
}