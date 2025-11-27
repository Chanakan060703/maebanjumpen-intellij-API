package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.person.dto.PersonDTO;
import com.itsci.mju.maebanjumpen.entity.Person;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    PersonDTO toDto(Person entity);
    Person toEntity(PersonDTO dto);
    List<PersonDTO> toDtoList(List<Person> entities);
}
