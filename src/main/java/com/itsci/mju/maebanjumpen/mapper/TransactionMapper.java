package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.TransactionDTO;
import com.itsci.mju.maebanjumpen.model.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")

public interface TransactionMapper {
    TransactionDTO toDto(Transaction entity);
    Transaction toEntity(TransactionDTO dto);
    List<TransactionDTO> toDtoList(List<Transaction> entities);
}
