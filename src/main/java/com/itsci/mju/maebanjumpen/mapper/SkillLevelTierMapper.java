package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.SkillLevelTierDTO;
import com.itsci.mju.maebanjumpen.model.SkillLevelTier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillLevelTierMapper {
    SkillLevelTierDTO toDto(SkillLevelTier entity);
    SkillLevelTier toEntity(SkillLevelTierDTO dto);
    List<SkillLevelTierDTO> toDtoList(List<SkillLevelTier> entities);
}
