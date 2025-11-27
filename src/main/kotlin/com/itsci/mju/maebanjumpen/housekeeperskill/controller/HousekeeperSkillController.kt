package com.itsci.mju.maebanjumpen.housekeeperskill.controller

import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO
import com.itsci.mju.maebanjumpen.housekeeperskill.service.HousekeeperSkillService
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/housekeeper-skills")
class HousekeeperSkillController(private val housekeeperSkillService: HousekeeperSkillService) {

    @GetMapping
    fun getAllHousekeeperSkills(): ResponseEntity<List<HousekeeperDTO>> {
        val skills = housekeeperSkillService.getAllHousekeeperSkills()
        return ResponseEntity.ok(skills)
    }

    @GetMapping("/{id}")
    fun getHousekeeperSkillById(@PathVariable id: Int): ResponseEntity<HousekeeperSkillDTO> {
        val skill = housekeeperSkillService.getHousekeeperSkillById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(skill)
    }

    @PostMapping
    fun createHousekeeperSkill(@RequestBody housekeeperSkillDto: HousekeeperSkillDTO): ResponseEntity<HousekeeperSkillDTO?> {
        return try {
            val createdSkill = housekeeperSkillService.saveHousekeeperSkill(housekeeperSkillDto)
            ResponseEntity.status(HttpStatus.CREATED).body(createdSkill)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @PutMapping("/{id}")
    fun updateHousekeeperSkill(@PathVariable id: Int, @RequestBody skillDto: HousekeeperSkillDTO): ResponseEntity<HousekeeperSkillDTO?> {
        return try {
            val updatedSkill = housekeeperSkillService.updateHousekeeperSkill(id, skillDto)
            ResponseEntity.ok(updatedSkill)
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteHousekeeperSkill(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        return try {
            if (housekeeperSkillService.getHousekeeperSkillById(id) == null) {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("status" to "error", "message" to "HousekeeperSkill not found with ID: $id"))
            } else {
                housekeeperSkillService.deleteHousekeeperSkill(id)
                ResponseEntity.ok(mapOf("status" to "success", "message" to "Skill deleted successfully"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("status" to "error", "message" to "Failed to delete skill: ${e.message}"))
        }
    }
}

