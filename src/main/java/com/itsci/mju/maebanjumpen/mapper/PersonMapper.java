package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.PersonDTO;
import com.itsci.mju.maebanjumpen.model.Person;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    PersonDTO toDto(Person entity);
    Person toEntity(PersonDTO dto);
    List<PersonDTO> toDtoList(List<Person> entities);
}
