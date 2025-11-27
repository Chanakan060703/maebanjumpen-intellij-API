package com.itsci.mju.maebanjumpen.mapper;


import com.itsci.mju.maebanjumpen.partyrole.dto.AdminDTO;
import com.itsci.mju.maebanjumpen.entity.Admin;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminDTO toDto(Admin entity);
    Admin toEntity(AdminDTO dto);
    List<AdminDTO> toDtoList(List<Admin> entities);
}
