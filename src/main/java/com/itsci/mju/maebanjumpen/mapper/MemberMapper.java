// File: src/main/java/com/itsci.mju.maebanjumpen.mapper/MemberMapper.java (ใช้งานได้แล้ว)

package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.MemberDTO;
import com.itsci.mju.maebanjumpen.model.Member;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface  MemberMapper {
    MemberDTO toDto(Member entity);
    Member toEntity(MemberDTO dto);
    List<MemberDTO> toList(List<Member> entities);
}