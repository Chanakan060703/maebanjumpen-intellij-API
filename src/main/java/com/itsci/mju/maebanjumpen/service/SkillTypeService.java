package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.SkillTypeDTO;
import java.util.List;

public interface SkillTypeService {
    // เปลี่ยน return type และ parameter เป็น DTO
    List<SkillTypeDTO> getAllSkillTypes();
    SkillTypeDTO getSkillTypeById(int id);
    SkillTypeDTO saveNewSkillType(SkillTypeDTO skillTypeDto);
    SkillTypeDTO updateSkillType(int id, SkillTypeDTO skillTypeDto);
    void deleteSkillType(int id);
}