package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.PenaltyDTO;
import com.itsci.mju.maebanjumpen.model.Penalty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ReportMapper.class})
public interface PenaltyMapper {
    // Mapping Report Entity ใน Model ไปเป็น reportId ใน DTO (ขาออก)
    @Mapping(target = "reportId", source = "report.reportId")
    PenaltyDTO toDto(Penalty entity);

    // Mapping ReportId ใน DTO ให้ข้ามการ mapping Report Entity ใน Model (ขาเข้า)
    // การผูก Report Entity จะทำใน Service แทน เพื่อให้มั่นใจว่าเป็น Managed Entity
    @Mapping(target = "report", ignore = true)
    Penalty toEntity(PenaltyDTO dto);

    List<PenaltyDTO> toDtoList(List<Penalty> entities);
}
