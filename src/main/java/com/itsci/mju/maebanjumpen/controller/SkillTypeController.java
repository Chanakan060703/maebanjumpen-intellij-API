package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.model.SkillType;
import com.itsci.mju.maebanjumpen.repository.SkillTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/maeban/skill-types")
public class SkillTypeController {

    @Autowired
    private SkillTypeRepository skillTypeRepository;

    @GetMapping
    public ResponseEntity<List<SkillType>> getAllSkillTypes() {
        return ResponseEntity.ok(skillTypeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillType> getSkillTypeById(@PathVariable int id) {
        Optional<SkillType> skillType = skillTypeRepository.findById(id);
        return skillType.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SkillType> createSkillType(@RequestBody SkillType skillType) {
        SkillType saved = skillTypeRepository.save(skillType);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillType> updateSkillType(@PathVariable int id, @RequestBody SkillType skillTypeDetails) {
        Optional<SkillType> optionalSkillType = skillTypeRepository.findById(id);

        if (optionalSkillType.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SkillType skillType = optionalSkillType.get();
        skillType.setSkillTypeName(skillTypeDetails.getSkillTypeName());
        skillType.setSkillTypeDetail(skillTypeDetails.getSkillTypeDetail());

        return ResponseEntity.ok(skillTypeRepository.save(skillType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkillType(@PathVariable int id) {
        if (!skillTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        skillTypeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
