package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.ReportDTO;
import com.itsci.mju.maebanjumpen.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PartyRoleMapper.class, HireMapper.class})
public interface ReportMapper {

    // 1. toDto: Map Hire entity ไปยัง HireDTO
    @Mapping(target = "hire", source = "hire")
    ReportDTO toDto(Report entity);

    // ----------------------------------------------------
// 2. toEntity: ลบ Mapping สำหรับ Hirer และ Housekeeper
// ----------------------------------------------------
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "penalty", ignore = true)
    @Mapping(target = "hire", ignore = true) // ต้องจัดการ Hire Entity ใน Service

    // ❌ ลบ
    // @Mapping(target = "hirer", ignore = false)
    // @Mapping(target = "housekeeper", ignore = false)

    Report toEntity(ReportDTO dto);

    List<ReportDTO> toDtoList(List<Report> entities);
}