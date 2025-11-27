package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillTypeDTO;
import com.itsci.mju.maebanjumpen.entity.SkillType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring") // ทำให้ Spring จัดการเป็น Bean
public interface SkillTypeMapper {
    SkillTypeDTO toDto(SkillType entity);
    SkillType toEntity(SkillTypeDTO dto);

    List<SkillTypeDTO> toDtoList(List<SkillType> entities);
}