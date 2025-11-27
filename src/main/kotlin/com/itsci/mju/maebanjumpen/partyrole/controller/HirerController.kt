package com.itsci.mju.maebanjumpen.partyrole.controller

import com.itsci.mju.maebanjumpen.partyrole.dto.HirerDTO
import com.itsci.mju.maebanjumpen.partyrole.service.HirerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/hirers")
class HirerController(private val hirerService: HirerService) {

    @GetMapping
    fun getAllHirers(): ResponseEntity<List<HirerDTO>> {
        val hirers = hirerService.getAllHirers()
        return ResponseEntity.ok(hirers)
    }

    @GetMapping("/{id}")
    fun getHirerById(@PathVariable id: Int): ResponseEntity<HirerDTO> {
        val hirer = hirerService.getHirerById(id)
        return ResponseEntity.ok(hirer)
    }

    @PostMapping
    fun createHirer(@RequestBody hirer: HirerDTO): ResponseEntity<HirerDTO> {
        val savedHirer = hirerService.saveHirer(hirer)
        return ResponseEntity.ok(savedHirer)
    }

    @PutMapping("/{id}")
    fun updateHirer(@PathVariable id: Int, @RequestBody hirer: HirerDTO): ResponseEntity<HirerDTO> {
        val updatedHirer = hirerService.updateHirer(id, hirer)
        return ResponseEntity.ok(updatedHirer)
    }

    @DeleteMapping("/{id}")
    fun deleteHirer(@PathVariable id: Int): ResponseEntity<Void> {
        hirerService.deleteHirer(id)
        return ResponseEntity.noContent().build()
    }
}

