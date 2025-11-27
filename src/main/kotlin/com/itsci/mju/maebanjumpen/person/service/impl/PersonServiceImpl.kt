package com.itsci.mju.maebanjumpen.person.service.impl

import com.itsci.mju.maebanjumpen.entity.Login
import com.itsci.mju.maebanjumpen.entity.Person
import com.itsci.mju.maebanjumpen.login.dto.LoginDTO
import com.itsci.mju.maebanjumpen.login.repository.LoginRepository
import com.itsci.mju.maebanjumpen.person.dto.PersonDTO
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import com.itsci.mju.maebanjumpen.person.service.PersonService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PersonServiceImpl(
    private val personRepository: PersonRepository,
    private val loginRepository: LoginRepository
) : PersonService {

    private fun mapPersonToDto(person: Person): PersonDTO {
        return PersonDTO(
            id = person.id,
            email = person.email,
            firstName = person.firstName,
            lastName = person.lastName,
            idCardNumber = person.idCardNumber,
            phoneNumber = person.phoneNumber,
            address = person.address,
            pictureUrl = person.pictureUrl,
            accountStatus = person.accountStatus,
            login = person.login?.let { LoginDTO(username = it.username, password = null) }
        )
    }

    private fun mapDtoToEntity(dto: PersonDTO): Person {
        return Person(
            id = dto.id,
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            idCardNumber = dto.idCardNumber,
            phoneNumber = dto.phoneNumber,
            address = dto.address,
            pictureUrl = dto.pictureUrl,
            accountStatus = dto.accountStatus,
            login = dto.login?.let { Login(username = it.username ?: "", password = it.password ?: "") }
        )
    }

    override fun getAllPersons(): List<PersonDTO> {
        return personRepository.findAll().map { mapPersonToDto(it) }
    }

    override fun getPersonById(id: Long): PersonDTO? {
        return personRepository.findById(id)
            .map { mapPersonToDto(it) }
            .orElse(null)
    }

    override fun getPersonByUsername(username: String): PersonDTO? {
        return personRepository.findByLoginUsername(username)
            .map { mapPersonToDto(it) }
            .orElse(null)
    }

    @Transactional
    override fun savePerson(personDto: PersonDTO): PersonDTO {
        val person = mapDtoToEntity(personDto)

        if (person.login != null && person.login?.username != null) {
            val existingLogin = loginRepository.findById(person.login!!.username)

            if (existingLogin.isPresent) {
                person.login = existingLogin.get()
            }
        }

        val savedPerson = personRepository.save(person)
        return mapPersonToDto(savedPerson)
    }

    @Transactional
    override fun updatePerson(id: Long, personDto: PersonDTO): PersonDTO {
        val existingPerson = personRepository.findById(id)
            .orElseThrow { RuntimeException("Person with ID $id not found") }

        personDto.email?.let { existingPerson.email = it }
        personDto.firstName?.let { existingPerson.firstName = it }
        personDto.lastName?.let { existingPerson.lastName = it }
        personDto.idCardNumber?.let { existingPerson.idCardNumber = it }
        personDto.phoneNumber?.let { existingPerson.phoneNumber = it }
        personDto.address?.let { existingPerson.address = it }
        personDto.pictureUrl?.let { existingPerson.pictureUrl = it }
        personDto.accountStatus?.let { existingPerson.accountStatus = it }

        if (personDto.login != null && personDto.login?.username != null) {
            val currentLoginOptional = loginRepository.findById(personDto.login!!.username!!)

            if (currentLoginOptional.isPresent) {
                existingPerson.login = currentLoginOptional.get()
            } else {
                val newLogin = mapDtoToEntity(personDto).login
                if (newLogin != null) {
                    loginRepository.save(newLogin)
                    existingPerson.login = newLogin
                }
            }
        }

        val updatedPerson = personRepository.save(existingPerson)
        return mapPersonToDto(updatedPerson)
    }

    @Transactional
    override fun updatePersonPictureUrl(id: Long, newBaseUrl: String): PersonDTO? {
        return personRepository.findById(id).map { person ->
            val oldPictureUrl = person.pictureUrl
            if (!oldPictureUrl.isNullOrEmpty()) {
                val lastSlashIndex = oldPictureUrl.indexOf("/maeban/files")
                if (lastSlashIndex != -1) {
                    val pathAndFile = oldPictureUrl.substring(lastSlashIndex)
                    val newPictureUrl = newBaseUrl + pathAndFile
                    person.pictureUrl = newPictureUrl
                    return@map mapPersonToDto(personRepository.save(person))
                }
            }
            mapPersonToDto(person)
        }.orElse(null)
    }

    @Transactional
    override fun deletePerson(id: Long) {
        personRepository.deleteById(id)
    }

    @Transactional
    override fun updateAccountStatus(personId: Long, newStatus: String) {
        val existingPerson = personRepository.findById(personId)
            .orElseThrow { RuntimeException("Person not found with ID: $personId") }

        if (newStatus.isNotBlank()) {
            existingPerson.accountStatus = newStatus
            personRepository.save(existingPerson)
        }
    }

    @Transactional
    override fun updateAllPersonPictureUrls(newBaseUrl: String) {
        val allPersons = personRepository.findAll()
        for (person in allPersons) {
            val oldPictureUrl = person.pictureUrl
            if (!oldPictureUrl.isNullOrEmpty()) {
                val lastSlashIndex = oldPictureUrl.indexOf("/maeban/files")
                if (lastSlashIndex != -1) {
                    val pathAndFile = oldPictureUrl.substring(lastSlashIndex)
                    val newPictureUrl = newBaseUrl + pathAndFile
                    person.pictureUrl = newPictureUrl
                }
            }
        }
        personRepository.saveAll(allPersons)
    }
}

