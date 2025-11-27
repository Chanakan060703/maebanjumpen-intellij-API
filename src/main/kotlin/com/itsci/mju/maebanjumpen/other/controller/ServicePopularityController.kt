package com.itsci.mju.maebanjumpen.other.controller

import com.itsci.mju.maebanjumpen.other.service.impl.ServicePopularityService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/maeban/services")
class ServicePopularityController(private val servicePopularityService: ServicePopularityService) {

    /**
     * API endpoint to retrieve popularity data for all service types.
     * The data includes average rating and total review count for each service.
     *
     * @return A ResponseEntity containing a Map of service popularity data.
     * Example:
     * {
     * "House Cleaning": { "rating": 4.8, "reviews": 2400 },
     * "Personal Cooking": { "rating": 4.5, "reviews": 1800 }
     * }
     */
    @GetMapping("/popularity")
    fun getServicePopularity(): ResponseEntity<Map<String, Map<String, Any>>> {
        val data = servicePopularityService.getServicePopularity()
        return ResponseEntity.ok(data)
    }
}

