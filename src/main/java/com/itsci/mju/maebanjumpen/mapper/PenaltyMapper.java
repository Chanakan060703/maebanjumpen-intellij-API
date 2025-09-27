package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.PenaltyDTO;
import com.itsci.mju.maebanjumpen.model.Penalty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ReportMapper.class}) // อาจต้องใช้ ReportMapper ด้วย
public interface PenaltyMapper {
    // ⬅️ เพิ่ม @Mapping เพื่อ Mapping ระหว่าง Report Entity กับ Report ID ใน DTO
    @Mapping(target = "reportId", source = "report.reportId")
    PenaltyDTO toDto(Penalty entity);

    @Mapping(target = "report", ignore = true)
        // บอกให้ MapStruct ไม่ต้องแมป Report Entity ขาเข้า (เพราะเราจะหาเองใน Service)
    Penalty toEntity(PenaltyDTO dto);

    List<PenaltyDTO> toDtoList(List<Penalty> entities);
}