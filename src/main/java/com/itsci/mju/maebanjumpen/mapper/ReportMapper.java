package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.ReportDTO;
import com.itsci.mju.maebanjumpen.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping; // 💡 ต้องเพิ่ม import นี้

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    // 1. Entity (มี Object) -> DTO (ใช้ ID)
    // แมปความสัมพันธ์จาก Object ใน Entity (เช่น report.reporter.id) ไปยัง ID ใน DTO (reporterId)
    @Mapping(target = "reporterId", source = "reporter.id")
    @Mapping(target = "hirerId", source = "hirer.id")
    @Mapping(target = "housekeeperId", source = "housekeeper.id")
    @Mapping(target = "penaltyId", source = "penalty.penaltyId")
    @Mapping(target = "hireId", source = "hire.hireId")
    ReportDTO toDto(Report entity);

    // 2. DTO (มี ID) -> Entity (ต้องการ Object)
    // เพิกเฉยต่อการแมป Object เพื่อให้ Service Layer ใช้ ID ที่รับมาไปค้นหา Object จริงจากฐานข้อมูล
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "hirer", ignore = true)
    @Mapping(target = "housekeeper", ignore = true)
    @Mapping(target = "penalty", ignore = true)
    @Mapping(target = "hire", ignore = true)
    Report toEntity(ReportDTO dto);

    List<ReportDTO> toDtoList(List<Report> entities);
}
