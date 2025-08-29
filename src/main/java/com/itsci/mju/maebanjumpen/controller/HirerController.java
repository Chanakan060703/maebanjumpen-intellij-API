package com.itsci.mju.maebanjumpen.controller;

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
    public ResponseEntity<List<Hirer>> getAllHirers() {
        List<Hirer> hirers = hirerService.getAllHirers();
        return ResponseEntity.ok(hirers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hirer> getHirerById(@PathVariable int id) {
        Hirer hirer = hirerService.getHirerById(id);
        return ResponseEntity.ok(hirer);
    }

    @PostMapping
    public ResponseEntity<Hirer> createHirer(@RequestBody Hirer hirer) {
        Hirer savedHirer = hirerService.saveHirer(hirer);
        return ResponseEntity.ok(savedHirer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hirer> updateHirer(@PathVariable int id, @RequestBody Hirer hirer) {
        Hirer updatedHirer = hirerService.updateHirer(id, hirer);
        return ResponseEntity.ok(updatedHirer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHirer(@PathVariable int id) {
        hirerService.deleteHirer(id);
        return ResponseEntity.noContent().build();
    }
}