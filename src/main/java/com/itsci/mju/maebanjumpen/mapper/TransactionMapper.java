package com.itsci.mju.maebanjumpen.mapper;// package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.TransactionDTO;
import com.itsci.mju.maebanjumpen.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    // 🚨 แก้ไข: ไม่ต้อง Map ID แยก และ Map Member Object โดยตรง
    // MapStruct จะจัดการ member object โดยอัตโนมัติหากชื่อ field ตรงกัน
    // แต่เราเพิ่ม @Mapping เพื่อความชัดเจน และเพื่อป้องกันการ Map field อื่นๆ ที่อาจจะชนกัน
    @Mapping(source = "member", target = "member")
    TransactionDTO toDto(Transaction entity);

    // เมื่อแปลงจาก DTO ไป Entity: Ignore 'member'
    @Mapping(target = "member", ignore = true)
    Transaction toEntity(TransactionDTO dto);

    List<TransactionDTO> toDtoList(List<Transaction> entities);
}