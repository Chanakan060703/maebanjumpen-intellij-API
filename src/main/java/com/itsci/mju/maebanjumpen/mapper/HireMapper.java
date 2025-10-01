package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.HireDTO;
import com.itsci.mju.maebanjumpen.model.Hire;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                HirerMapper.class,
                // HousekeeperMapper.class ถูกลบออกจาก uses แล้ว
                SkillTypeMapper.class,
                ReviewMapper.class
        }
)
public interface HireMapper {
    HireDTO toDto(Hire entity);

    Hire toEntity(HireDTO dto);

    List<HireDTO> toDtoList(List<Hire> entities);
}