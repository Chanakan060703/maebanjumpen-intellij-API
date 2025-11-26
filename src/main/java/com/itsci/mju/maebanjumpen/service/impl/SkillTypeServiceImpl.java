package com.itsci.mju.maebanjumpen.service.impl;

// ใช้ DTO package ที่ถูกต้องเพียงอันเดียว (สมมติว่าเป็น com.itsci.mju.maebanjumpen.dto)
import com.itsci.mju.maebanjumpen.dto.SkillTypeDTO;
import com.itsci.mju.maebanjumpen.mapper.SkillTypeMapper;
import com.itsci.mju.maebanjumpen.model.SkillType;
import com.itsci.mju.maebanjumpen.repository.SkillTypeRepository;
import com.itsci.mju.maebanjumpen.service.SkillTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SkillTypeServiceImpl implements SkillTypeService {

    private final SkillTypeRepository skillTypeRepository;
    private final SkillTypeMapper skillTypeMapper;

    @Override
    public List<SkillTypeDTO> getAllSkillTypes() {
        List<SkillType> entities = skillTypeRepository.findAll();
        return skillTypeMapper.toDtoList(entities);
    }

    @Override
    public SkillTypeDTO getSkillTypeById(int id) {
        SkillType entity = skillTypeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("SkillType not found with ID: " + id));
        return skillTypeMapper.toDto(entity);
    }

    @Override
    public SkillTypeDTO saveNewSkillType(SkillTypeDTO skillTypeDto) {
        SkillType entity = skillTypeMapper.toEntity(skillTypeDto);

        SkillType savedEntity = skillTypeRepository.save(entity);

        return skillTypeMapper.toDto(savedEntity);
    }

    @Override
    public SkillTypeDTO updateSkillType(int id, SkillTypeDTO skillTypeDto) {
        SkillType existingSkillType = skillTypeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("SkillType not found with ID: " + id));

        existingSkillType.setSkillTypeName(skillTypeDto.getSkillTypeName());
        existingSkillType.setSkillTypeDetail(skillTypeDto.getSkillTypeDetail());

        SkillType updatedEntity = skillTypeRepository.save(existingSkillType);

        return skillTypeMapper.toDto(updatedEntity);
    }

    @Override
    public void deleteSkillType(int id) {
        if (!skillTypeRepository.existsById(id)) {
            // โยน Exception แทนการคืนค่า null/boolean เพื่อให้ Controller จัดการสถานะ 404 ได้ง่ายขึ้น
            throw new NoSuchElementException("SkillType not found with ID: " + id);
        }
        skillTypeRepository.deleteById(id);
    }
}