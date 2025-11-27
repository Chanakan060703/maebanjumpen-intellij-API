package com.itsci.mju.maebanjumpen.skilltype.controller;

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillLevelTierDTO;
import com.itsci.mju.maebanjumpen.skilltype.service.SkillLevelTierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/maeban/skill-level-tiers")
@RequiredArgsConstructor
public class SkillLevelTierController {

    private final SkillLevelTierService skillLevelTierService;

    @GetMapping
    public ResponseEntity<List<SkillLevelTierDTO>> getAllSkillLevelTiers() {
        List<SkillLevelTierDTO> tiers = skillLevelTierService.getAllSkillLevelTiers();
        return ResponseEntity.ok(tiers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillLevelTierDTO> getSkillLevelTierById(@PathVariable Integer id) {
        SkillLevelTierDTO tier = skillLevelTierService.getSkillLevelTierById(id);
        if (tier == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tier);
    }

    @PostMapping
    public ResponseEntity<SkillLevelTierDTO> createSkillLevelTier(@RequestBody SkillLevelTierDTO skillLevelTierDTO) {
        try {
            SkillLevelTierDTO savedTier = skillLevelTierService.createSkillLevelTier(skillLevelTierDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTier);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating skill level tier", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillLevelTierDTO> updateSkillLevelTier(@PathVariable Integer id, @RequestBody SkillLevelTierDTO skillLevelTierDTO) {
        try {
            SkillLevelTierDTO updatedTier = skillLevelTierService.updateSkillLevelTier(id, skillLevelTierDTO);
            return ResponseEntity.ok(updatedTier);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkillLevelTier(@PathVariable Integer id) {
        try {
            skillLevelTierService.deleteSkillLevelTier(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}