package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO;
import com.itsci.mju.maebanjumpen.entity.HousekeeperSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface HousekeeperSkillMapper {

    // --- DTO to Entity Mappings (FIX: เพิ่มเมธอดนี้เพื่อรองรับการ save/update ใน Service) ---
    HousekeeperSkill toEntity(HousekeeperSkillDTO dto);
    List<HousekeeperSkill> toEntityList(List<HousekeeperSkillDTO> dtos);

    // --- Entity to DTO Mappings (โค้ดเดิมที่แก้ไขปัญหาค่า null) ---
    @Mapping(target = "housekeeperId", source = "housekeeper.id")
    @Mapping(target = "skillLevelTierId", source = "skillLevelTier.id")
    @Mapping(target = "skillTypeId", source = "skillType.skillTypeId")

    HousekeeperSkillDTO toDto(HousekeeperSkill entity);

    List<HousekeeperSkillDTO> toDtoList(List<HousekeeperSkill> entities);
}
