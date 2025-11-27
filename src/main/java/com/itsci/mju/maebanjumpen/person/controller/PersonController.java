package com.itsci.mju.maebanjumpen.person.controller;

import com.itsci.mju.maebanjumpen.person.dto.PersonDTO;
import com.itsci.mju.maebanjumpen.person.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maeban/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonDTO>> getAllPersons() {
        List<PersonDTO> persons = personService.getAllPersons();
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable int id) {
        PersonDTO person = personService.getPersonById(id);
        return ResponseEntity.ok(person);
    }

    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@RequestBody PersonDTO person) {
        PersonDTO savedPerson = personService.savePerson(person);
        return ResponseEntity.ok(savedPerson);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable int id, @RequestBody PersonDTO person) {
        PersonDTO updatedPerson = personService.updatePerson(id, person);
        return ResponseEntity.ok(updatedPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable int id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/update-picture-url")
    public ResponseEntity<PersonDTO> updatePersonPictureUrl(
            @PathVariable int id,
            @RequestParam String newBaseUrl) {
        PersonDTO updatedPerson = personService.updatePersonPictureUrl(id, newBaseUrl);
        if (updatedPerson != null) {
            return ResponseEntity.ok(updatedPerson);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update-all-picture-urls")
    public ResponseEntity<String> updateAllPersonPictureUrls(
            @RequestParam String newBaseUrl) {
        try {
            personService.updateAllPersonPictureUrls(newBaseUrl);
            return ResponseEntity.ok("All person picture URLs updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update URLs: " + e.getMessage());
        }
    }
}