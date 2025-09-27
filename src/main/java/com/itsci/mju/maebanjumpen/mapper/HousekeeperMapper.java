package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.HousekeeperDTO;
import com.itsci.mju.maebanjumpen.dto.HousekeeperDetailDTO;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {HousekeeperSkillMapper.class, ReviewMapper.class, HireMapper.class}) // 💡 เพิ่ม HireMapper เพื่อรองรับ List<HireDTO>
public interface HousekeeperMapper {

    Housekeeper toEntity(HousekeeperDTO dto);
    List<Housekeeper> toEntityList(List<HousekeeperDTO> dtos);

    @Mapping(target = "username", source = "person.login.username")
    // เปลี่ยน entity เป็น housekeeper
    @Mapping(target = "hireIds", expression = "java(housekeeper.getHires() != null ? housekeeper.getHires().stream().map(com.itsci.mju.maebanjumpen.model.Hire::getHireId).collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())")
    @Named("baseHousekeeperMapping")
    HousekeeperDTO toDto(Housekeeper housekeeper); // เปลี่ยน entity เป็น housekeeper

    @Mapping(target = "username", source = "person.login.username")
    // เปลี่ยน entity เป็น housekeeper
    @Mapping(target = "hireIds", expression = "java(housekeeper.getHires() != null ? housekeeper.getHires().stream().map(com.itsci.mju.maebanjumpen.model.Hire::getHireId).collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())")

    // เปลี่ยน entity เป็น housekeeper
    @Mapping(target = "jobsCompleted", expression = "java(housekeeper.getHires() != null ? (int) housekeeper.getHires().stream().filter(h -> \"Completed\".equals(h.getJobStatus()) || \"Reviewed\".equals(h.getJobStatus())).count() : 0)")
    @Mapping(target = "reviews", ignore = true) // Map reviews ใน Service
    @Mapping(target = "hires", source = "hires") // Map List<Hire> -> List<HireDTO>
    HousekeeperDetailDTO toDetailDto(Housekeeper housekeeper);

    @IterableMapping(qualifiedByName = "baseHousekeeperMapping")
    List<HousekeeperDTO> toDtoList(List<Housekeeper> entities);
}