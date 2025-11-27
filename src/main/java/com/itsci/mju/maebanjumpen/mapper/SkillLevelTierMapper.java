package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillLevelTierDTO;
import com.itsci.mju.maebanjumpen.entity.SkillLevelTier;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillLevelTierMapper {
    SkillLevelTierDTO toDto(SkillLevelTier entity);
    SkillLevelTier toEntity(SkillLevelTierDTO dto);
    List<SkillLevelTierDTO> toDtoList(List<SkillLevelTier> entities);
}
