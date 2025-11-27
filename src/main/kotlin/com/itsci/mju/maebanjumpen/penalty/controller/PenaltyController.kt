package com.itsci.mju.maebanjumpen.penalty.controller

import com.itsci.mju.maebanjumpen.penalty.dto.PenaltyDTO
import com.itsci.mju.maebanjumpen.penalty.service.PenaltyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/penalties")
class PenaltyController(private val penaltyService: PenaltyService) {

    @PostMapping
    fun createPenalty(
        @RequestBody penaltyDTO: PenaltyDTO,
        @RequestParam("reportId") reportId: Int,
        @RequestParam(value = "hirerId", required = false) hirerId: Int?,
        @RequestParam(value = "housekeeperId", required = false) housekeeperId: Int?
    ): ResponseEntity<PenaltyDTO> {
        val targetRoleId: Int? = when {
            hirerId != null -> hirerId
            housekeeperId != null -> housekeeperId
            else -> {
                System.err.println("Error: Missing target Role ID (hirerId or housekeeperId) for penalty creation.")
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }
        }

        penaltyDTO.reportId = reportId

        return try {
            val createdPenalty = penaltyService.savePenalty(penaltyDTO, targetRoleId!!)
            ResponseEntity(createdPenalty, HttpStatus.CREATED)
        } catch (e: IllegalArgumentException) {
            System.err.println("Error creating penalty: ${e.message}")
            ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            System.err.println("Unexpected error creating penalty: ${e.message}")
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping
    fun getAllPenalties(): ResponseEntity<List<PenaltyDTO>> {
        val penalties = penaltyService.getAllPenalties()
        return ResponseEntity(penalties, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getPenaltyById(@PathVariable id: Int): ResponseEntity<PenaltyDTO> {
        val penalty = penaltyService.getPenaltyById(id)
        return if (penalty != null) {
            ResponseEntity(penalty, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/{id}")
    fun updatePenalty(@PathVariable id: Int, @RequestBody penaltyDTO: PenaltyDTO): ResponseEntity<PenaltyDTO> {
        return try {
            val updatedPenalty = penaltyService.updatePenalty(id, penaltyDTO)
            ResponseEntity(updatedPenalty, HttpStatus.OK)
        } catch (e: RuntimeException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/{id}")
    fun deletePenalty(@PathVariable id: Int): ResponseEntity<Void> {
        return try {
            penaltyService.deletePenalty(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}

