package com.itsci.mju.maebanjumpen.housekeeperskill.controller

import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperDetailDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import com.itsci.mju.maebanjumpen.partyrole.service.HousekeeperService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/housekeepers")
class HousekeeperController(private val housekeeperService: HousekeeperService) {

    @GetMapping
    fun getAllHousekeepers(): ResponseEntity<List<HousekeeperDTO>> {
        val housekeepers = housekeeperService.getAllHousekeepers()
        return ResponseEntity.ok(housekeepers)
    }

    @GetMapping("/{id}")
    fun getHousekeeperDetailById(@PathVariable id: Int): ResponseEntity<HousekeeperDetailDTO> {
        val housekeeper = housekeeperService.getHousekeeperDetailById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(housekeeper)
    }

    @GetMapping("/status/{status}")
    fun getHousekeepersByStatus(@PathVariable status: String): ResponseEntity<List<HousekeeperDTO>> {
        val housekeepers = housekeeperService.getHousekeepersByStatus(status)
        return ResponseEntity.ok(housekeepers)
    }

    @GetMapping("/unverified-or-null")
    fun getUnverifiedOrNullStatusHousekeepers(): ResponseEntity<List<HousekeeperDTO>> {
        val housekeepers = housekeeperService.getNotVerifiedOrNullStatusHousekeepers()
        return ResponseEntity.ok(housekeepers)
    }

    @PostMapping
    fun createHousekeeper(@RequestBody housekeeper: HousekeeperDTO): ResponseEntity<HousekeeperDTO> {
        val savedHousekeeper = housekeeperService.saveHousekeeper(housekeeper)
        return ResponseEntity.ok(savedHousekeeper)
    }

    @PutMapping("/{id}")
    fun updateHousekeeper(@PathVariable id: Int, @RequestBody housekeeper: HousekeeperDTO): ResponseEntity<HousekeeperDTO> {
        val updatedHousekeeper = housekeeperService.updateHousekeeper(id, housekeeper)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updatedHousekeeper)
    }

    @DeleteMapping("/{id}")
    fun deleteHousekeeper(@PathVariable id: Int): ResponseEntity<Void> {
        housekeeperService.deleteHousekeeper(id)
        return ResponseEntity.noContent().build()
    }
}

