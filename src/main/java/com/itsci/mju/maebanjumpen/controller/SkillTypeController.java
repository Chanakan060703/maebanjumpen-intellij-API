package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.SkillTypeDTO;
import com.itsci.mju.maebanjumpen.service.SkillTypeService; // ใช้ Service Interface
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/maeban/skill-types")
@RequiredArgsConstructor // ใช้แทน @Autowired
public class SkillTypeController {

    private final SkillTypeService skillTypeService; // ใช้ Service Interface

    @GetMapping
    public ResponseEntity<List<SkillTypeDTO>> getAllSkillTypes() {
        // ใช้ DTO List
        List<SkillTypeDTO> skillTypes = skillTypeService.getAllSkillTypes();
        return ResponseEntity.ok(skillTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillTypeDTO> getSkillTypeById(@PathVariable int id) {
        try {
            // ใช้ DTO
            SkillTypeDTO skillType = skillTypeService.getSkillTypeById(id);
            return ResponseEntity.ok(skillType);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<SkillTypeDTO> createSkillType(@RequestBody SkillTypeDTO skillTypeDto) {
        // รับ DTO และส่งคืน DTO
        SkillTypeDTO saved = skillTypeService.saveNewSkillType(skillTypeDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED); // ควรคืนสถานะ 201 CREATED
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillTypeDTO> updateSkillType(@PathVariable int id, @RequestBody SkillTypeDTO skillTypeDto) {
        try {
            // รับ DTO และส่งคืน DTO
            SkillTypeDTO updated = skillTypeService.updateSkillType(id, skillTypeDto);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkillType(@PathVariable int id) {
        try {
            skillTypeService.deleteSkillType(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}