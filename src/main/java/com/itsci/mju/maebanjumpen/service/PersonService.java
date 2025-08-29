package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Person;

import java.util.List;

public interface PersonService {
    List<Person> getAllPersons();
    Person getPersonById(int id);
    Person savePerson(Person person);
    void deletePerson(int id);
    Person getPersonByUsername(String username);

    Person updatePerson(int id, Person person);
}