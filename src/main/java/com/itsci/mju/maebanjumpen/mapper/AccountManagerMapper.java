package com.itsci.mju.maebanjumpen.mapper;


import com.itsci.mju.maebanjumpen.dto.AccountManagerDTO;
import com.itsci.mju.maebanjumpen.model.AccountManager;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountManagerMapper {
    AccountManagerDTO toDto(AccountManager entity);
    AccountManager toEntity(AccountManagerDTO dto);
    List<AccountManagerDTO> toDtoList(List<AccountManager > entities);
}
