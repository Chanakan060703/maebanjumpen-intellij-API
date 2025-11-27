package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO;
import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperDetailDTO;
import com.itsci.mju.maebanjumpen.entity.Housekeeper;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.Set; // 💡 ต้อง Import Set
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {HousekeeperSkillMapper.class, ReviewMapper.class, HireMapper.class})
public interface HousekeeperMapper {

    Housekeeper toEntity(HousekeeperDTO dto);
    List<Housekeeper> toEntityList(List<HousekeeperDTO> dtos);

    // 🎯 toDto: สำหรับ List หรือข้อมูลพื้นฐาน
    @Mapping(target = "username", source = "person.login.username")
    // 💡 ปรับให้เรียกใช้ Helper method (mapHiresToIds)
    @Mapping(target = "hireIds", source = "hires", qualifiedByName = "hiresToIds")
    @Named("baseHousekeeperMapping")
    HousekeeperDTO toDto(Housekeeper housekeeper);

    // 🎯 toDetailDto: สำหรับหน้ารายละเอียด
    @Mapping(target = "username", source = "person.login.username")

    // 💡 เนื่องจาก HousekeeperDetailDTO มี List<HireDTO> hires แล้ว เราไม่จำเป็นต้อง map เป็น hireIds ใน DTO ตัวนี้
    @Mapping(target = "hireIds", ignore = true)

    // 🎯 คำนวณ jobsCompleted
    @Mapping(target = "jobsCompleted", expression = "java(housekeeper.getHires() != null ? (int) housekeeper.getHires().stream().filter(h -> \"Completed\".equals(h.getJobStatus()) || \"Reviewed\".equals(h.getJobStatus())).count() : 0)")

    // 🎯 Map hires (Set<Hire>) -> hires (List<HireDTO>) โดยอัตโนมัติ (ใช้ HireMapper)
    @Mapping(target = "hires", source = "hires")

    // reviews ถูก Map ใน Service ตามตรรกะที่ออกแบบไว้
    @Mapping(target = "reviews", ignore = true)

    // dailyRate (String) และฟิลด์อื่นๆ ที่ชื่อตรงกันจะถูก Map โดยอัตโนมัติ
    HousekeeperDetailDTO toDetailDto(Housekeeper housekeeper);

    @IterableMapping(qualifiedByName = "baseHousekeeperMapping")
    List<HousekeeperDTO> toDtoList(List<Housekeeper> entities);

    // 🎯 NEW: Helper method สำหรับแปลง Set<Hire> เป็น List<Integer>
    @Named("hiresToIds")
    default List<Integer> mapHiresToIds(Set<com.itsci.mju.maebanjumpen.entity.Hire> hires) {
        if (hires == null) {
            return java.util.Collections.emptyList();
        }
        // ใช้ getId แทน getHireId เพื่ออ้างอิง Primary Key ID ของ Hire
        return hires.stream().map(com.itsci.mju.maebanjumpen.entity.Hire::getHireId).collect(Collectors.toList());
    }
}