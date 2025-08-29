package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.SkillType;

import java.util.List;

public interface SkillTypeService {
    List<SkillType> getAllSkillTypes();
    SkillType getSkillTypeById(int id);
    SkillType saveSkillType(SkillType skillType);
    void deleteSkillType(int id);
}