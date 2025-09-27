package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.HirerDTO;
import com.itsci.mju.maebanjumpen.model.Hirer;
import com.itsci.mju.maebanjumpen.model.Hire; // ต้อง Import Hire
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface HirerMapper {

    // 🎯 แก้ไข: กำหนด source สำหรับ username และ hireIds
    @Mapping(source = "person.login.username", target = "username") // ดึง username จาก Object login ใน person
    @Mapping(source = "hires", target = "hireIds", qualifiedByName = "hiresToIds") // แปลง Set<Hire> เป็น Set<Integer>
    HirerDTO toDto(Hirer entity);

    // เมธอดสำหรับแปลงกลับ (ไม่ต้องมี Mapping พิเศษสำหรับ username/hireIds)
    Hirer toEntity(HirerDTO dto);

    // แปลง List ของ Entity เป็น List ของ DTO
    List<HirerDTO> toDtoList(List<Hirer> entities);

    // 💡 Helper method เพื่อดึงเฉพาะ ID ของ Hire
    @Named("hiresToIds")
    default Set<Integer> mapHiresToIds(Set<Hire> hires) {
        if (hires == null) {
            return null;
        }
        return hires.stream()
                .map(Hire:: getHireId) // สมมติว่าเมธอด Get ID ใน Hire Entity คือ getId()
                .collect(Collectors.toSet());
    }
}