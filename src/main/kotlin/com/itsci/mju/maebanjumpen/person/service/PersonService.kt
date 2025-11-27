package com.itsci.mju.maebanjumpen.person.service

import com.itsci.mju.maebanjumpen.person.dto.PersonDTO

interface PersonService {
    fun getAllPersons(): List<PersonDTO>
    fun getPersonById(id: Long): PersonDTO?
    fun savePerson(personDto: PersonDTO): PersonDTO
    fun deletePerson(id: Long)
    fun getPersonByUsername(username: String): PersonDTO?
    fun updatePerson(id: Long, personDto: PersonDTO): PersonDTO
    fun updatePersonPictureUrl(id: Long, newBaseUrl: String): PersonDTO?
    fun updateAllPersonPictureUrls(newBaseUrl: String)
    fun updateAccountStatus(personId: Long, newStatus: String)
}

