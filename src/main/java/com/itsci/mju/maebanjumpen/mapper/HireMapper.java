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

    // 🎯 การแก้ไข/ยืนยัน: เพิ่ม @Mapping เพื่อแมป AdditionalSkillType (ถ้า DTO/Entity มี List<SkillType>)
    // (สมมติว่า Entity มี List<SkillType> additionalSkillTypes)
    // @Mapping(target = "additionalSkillTypeIds", source = "additionalSkillTypes") // ⚠️ อาจต้องปรับตามโครงสร้าง Entity จริง

    // หากฟิลด์ใน Entity/DTO ชื่อตรงกัน MapStruct จะจัดการเองทั้งหมดแล้ว (รวมถึง review)
    HireDTO toDto(Hire entity);

    Hire toEntity(HireDTO dto);

    List<HireDTO> toDtoList(List<Hire> entities);
}