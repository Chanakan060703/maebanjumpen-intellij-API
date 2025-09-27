package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.PersonDTO;
import com.itsci.mju.maebanjumpen.model.Person;

import java.util.List;

public interface PersonService {
    List<PersonDTO> getAllPersons();
    PersonDTO getPersonById(int id);
    PersonDTO savePerson(PersonDTO personDto);
    void deletePerson(int id);
    PersonDTO getPersonByUsername(String username);
    PersonDTO updatePerson(int id, PersonDTO personDto);
    PersonDTO updatePersonPictureUrl(int id, String newBaseUrl);
    // เพิ่มเมธอดนี้สำหรับอัปเดต URL ของทุก Person
    void updateAllPersonPictureUrls(String newBaseUrl);

    void updateAccountStatus(int personId, String newStatus); // ⬅️ เพิ่มเมธอดนี้

}