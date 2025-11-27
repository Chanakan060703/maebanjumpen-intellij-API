package com.itsci.mju.maebanjumpen.hire.controller


import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.hire.service.HireService
import com.luca.intern.common.exception.BadRequestException
import com.luca.intern.common.exception.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/hires")
class HireController(private val hireService: HireService) {

    @GetMapping
    fun getAllHires(): ResponseEntity<List<HireDTO>> {
        return try {
            val hires = hireService.getAllHires()
            ResponseEntity.ok(hires)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/{id}")
    fun getHireById(@PathVariable id: String): ResponseEntity<*> {
        return try {
            val hireId = id.toInt()
            val hire = hireService.getHireById(hireId)
            if (hire != null) ResponseEntity.ok(hire) else ResponseEntity.notFound().build<Any>()
        } catch (e: NumberFormatException) {
            ResponseEntity.badRequest().body(mapOf("error" to "Invalid hireId format"))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().build<Any>()
        }
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createHire(@RequestBody hire: HireDTO): ResponseEntity<*> {
        if (hire.hireDetail.isNullOrBlank()) {
            return ResponseEntity.badRequest().body(mapOf("error" to "hireDetail is required."))
        }
        if (hire.hireName.isNullOrBlank()) {
            return ResponseEntity.badRequest().body(mapOf("error" to "hireName is required."))
        }
        if (hire.paymentAmount == null || hire.paymentAmount!! <= 0) {
            return ResponseEntity.badRequest().body(mapOf("error" to "paymentAmount must be a positive value."))
        }
        if (hire.startDate == null) {
            return ResponseEntity.badRequest().body(mapOf("error" to "startDate is required."))
        }
        if (hire.hirer?.id == null) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Hirer ID is required."))
        }
        if (hire.housekeeper?.id == null) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Housekeeper ID is required."))
        }

        println("Received Hire for creation: $hire")
        hire.hirer?.let { println("Hirer ID: ${it.id}") }
        hire.housekeeper?.let { println("Housekeeper ID: ${it.id}") }

        return try {
            val savedHire = hireService.saveHire(hire)
            ResponseEntity(savedHire, HttpStatus.CREATED)
        } catch (e: NotFoundException) {
            System.err.println("Error creating hire: ${e.message}")
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        } catch (e: BadRequestException) {
            System.err.println("Error creating hire: ${e.message}")
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        } catch (e: IllegalArgumentException) {
            System.err.println("Error creating hire: ${e.message}")
            e.printStackTrace()
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            System.err.println("Unexpected error creating hire: ${e.message}")
            e.printStackTrace()
            ResponseEntity.internalServerError().body(mapOf("error" to "An unexpected error occurred: ${e.message}"))
        }
    }

    @PutMapping("/{hireId}")
    fun updateHire(@PathVariable hireId: Int, @RequestBody hireDetailsFromClient: HireDTO): ResponseEntity<*> {
        return try {
            val updatedHire = hireService.updateHire(hireId, hireDetailsFromClient)
            if (updatedHire != null) ResponseEntity.ok(updatedHire) else ResponseEntity.notFound().build<Any>()
        } catch (e: InsufficientBalanceException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        } catch (e: HirerNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        } catch (e: HousekeeperNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().build<Any>()
        }
    }

    @PatchMapping(path = ["/{hireId}/add-progression-images"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addProgressionImagesToHire(@PathVariable hireId: Int, @RequestBody imageUrls: List<String>): ResponseEntity<*> {
        return try {
            println("Received request to add progression images for hireId: $hireId")
            println("Image URLs: $imageUrls")
            val updatedHire = hireService.addProgressionImagesToHire(hireId, imageUrls)
            ResponseEntity.ok(updatedHire)
        } catch (e: IllegalArgumentException) {
            System.err.println("Error adding progression images: ${e.message}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message))
        } catch (e: Exception) {
            System.err.println("Unexpected error adding progression images: ${e.message}")
            e.printStackTrace()
            ResponseEntity.internalServerError().body(mapOf("error" to "An unexpected error occurred: ${e.message}"))
        }
    }

    @DeleteMapping("/{id}")
    fun deleteHire(@PathVariable id: Int): ResponseEntity<Void> {
        return try {
            hireService.deleteHire(id)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/hirer/{hirerId}")
    fun getHiresByHirerId(@PathVariable hirerId: Int): ResponseEntity<*> {
        return try {
            val hires = hireService.getHiresByHirerId(hirerId)
            ResponseEntity.ok(hires)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().build<Any>()
        }
    }

    @GetMapping("/housekeepers/{housekeeperId}")
    fun getHiresByHousekeeperId(@PathVariable housekeeperId: Int): ResponseEntity<*> {
        return try {
            val hires = hireService.getHiresByHousekeeperId(housekeeperId)
            ResponseEntity.ok(hires)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().build<Any>()
        }
    }

    @GetMapping("/housekeepers/{housekeeperId}/completed")
    fun getCompletedHiresByHousekeeperId(@PathVariable housekeeperId: Int): ResponseEntity<*> {
        return try {
            val hires = hireService.getCompletedHiresByHousekeeperId(housekeeperId)
            ResponseEntity.ok(hires)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().build<Any>()
        }
    }
}

