package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.LoginDTO;
import com.itsci.mju.maebanjumpen.model.Login;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoginMapper {
    LoginDTO toDto(Login entity);
    Login toEntity(LoginDTO dto);
    List<LoginDTO> toDtoList(List<Login> entities);
}