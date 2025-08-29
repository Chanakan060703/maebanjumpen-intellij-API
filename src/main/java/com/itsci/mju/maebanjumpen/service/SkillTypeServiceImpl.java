package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.SkillType;
import com.itsci.mju.maebanjumpen.repository.SkillTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillTypeServiceImpl implements SkillTypeService {

    @Autowired
    private SkillTypeRepository skillTypeRepository;

    @Override
    public List<SkillType> getAllSkillTypes() {
        return skillTypeRepository.findAll();
    }

    @Override
    public SkillType getSkillTypeById(int id) {
        return skillTypeRepository.findById(id).orElse(null);
    }

    @Override
    public SkillType saveSkillType(SkillType skillType) {
        return skillTypeRepository.save(skillType);
    }

    @Override
    public void deleteSkillType(int id) {
        skillTypeRepository.deleteById(id);
    }
}