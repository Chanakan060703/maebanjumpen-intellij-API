package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.ReportDTO;
import com.itsci.mju.maebanjumpen.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

// 1. เพิ่ม uses = {PartyRoleMapper.class} เพื่อให้ใช้เมธอด toDto(PartyRole) ใน PartyRoleMapper
@Mapper(componentModel = "spring", uses = {PartyRoleMapper.class})
public interface ReportMapper {

    @Mapping(target = "hireId", source = "hire.hireId")
    ReportDTO toDto(Report entity);


    @Mapping(target = "reporter", ignore = true) // ต้องจัดการ Entity ใน Service
    @Mapping(target = "hirer", ignore = true)
    @Mapping(target = "housekeeper", ignore = true)
    @Mapping(target = "penalty", ignore = true) // ต้องจัดการ Entity ใน Service
    @Mapping(target = "hire", ignore = true) // ต้องจัดการ Entity ใน Service
    Report toEntity(ReportDTO dto);

    List<ReportDTO> toDtoList(List<Report> entities);
}
