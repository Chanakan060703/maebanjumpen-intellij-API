package com.itsci.mju.maebanjumpen.person.controller

import com.itsci.mju.maebanjumpen.person.dto.PersonDTO
import com.itsci.mju.maebanjumpen.person.service.PersonService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/persons")
class PersonController(private val personService: PersonService) {

    @GetMapping
    fun getAllPersons(): ResponseEntity<List<PersonDTO>> {
        val persons = personService.getAllPersons()
        return ResponseEntity.ok(persons)
    }

    @GetMapping("/{id}")
    fun getPersonById(@PathVariable id: Int): ResponseEntity<PersonDTO> {
        val person = personService.getPersonById(id)
        return ResponseEntity.ok(person)
    }

    @PostMapping
    fun createPerson(@RequestBody person: PersonDTO): ResponseEntity<PersonDTO> {
        val savedPerson = personService.savePerson(person)
        return ResponseEntity.ok(savedPerson)
    }

    @PutMapping("/{id}")
    fun updatePerson(@PathVariable id: Int, @RequestBody person: PersonDTO): ResponseEntity<PersonDTO> {
        val updatedPerson = personService.updatePerson(id, person)
        return ResponseEntity.ok(updatedPerson)
    }

    @DeleteMapping("/{id}")
    fun deletePerson(@PathVariable id: Int): ResponseEntity<Void> {
        personService.deletePerson(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}/update-picture-url")
    fun updatePersonPictureUrl(
        @PathVariable id: Int,
        @RequestParam newBaseUrl: String
    ): ResponseEntity<PersonDTO> {
        val updatedPerson = personService.updatePersonPictureUrl(id, newBaseUrl)
        return if (updatedPerson != null) {
            ResponseEntity.ok(updatedPerson)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/update-all-picture-urls")
    fun updateAllPersonPictureUrls(@RequestParam newBaseUrl: String): ResponseEntity<String> {
        return try {
            personService.updateAllPersonPictureUrls(newBaseUrl)
            ResponseEntity.ok("All person picture URLs updated successfully.")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Failed to update URLs: ${e.message}")
        }
    }
}

