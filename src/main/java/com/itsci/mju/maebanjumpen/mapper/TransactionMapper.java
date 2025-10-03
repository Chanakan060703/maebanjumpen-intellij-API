package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.TransactionDTO;
import com.itsci.mju.maebanjumpen.dto.MemberDTO; // 🎯 ต้อง Import DTO ที่ใช้
import com.itsci.mju.maebanjumpen.model.Transaction;
import com.itsci.mju.maebanjumpen.model.Member; // 🎯 ต้อง Import Entity ที่ใช้
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "person", target = "person")
    MemberDTO memberToMemberDTO(Member member);

    @Mapping(source = "member", target = "member")
    TransactionDTO toDto(Transaction entity);

    @Mapping(target = "member", ignore = true)
    Transaction toEntity(TransactionDTO dto);

    List<TransactionDTO> toDtoList(List<Transaction> entities);
}