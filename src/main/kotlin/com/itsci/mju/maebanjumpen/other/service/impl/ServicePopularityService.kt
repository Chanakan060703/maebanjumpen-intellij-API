package com.itsci.mju.maebanjumpen.other.service.impl

import com.itsci.mju.maebanjumpen.other.repository.ServicePopularityRepository
import org.springframework.stereotype.Service

@Service
class ServicePopularityService(private val servicePopularityRepository: ServicePopularityRepository) {

    fun getServicePopularity(): Map<String, Map<String, Any>> {
        val results = servicePopularityRepository.findServicePopularityData()
        val popularityMap = mutableMapOf<String, Map<String, Any>>()

        for (result in results) {
            val serviceName = result[0] as String
            val avgRating = (result[1] as? Number)?.toDouble() ?: 0.0
            val reviewCount = (result[2] as? Number)?.toLong() ?: 0L

            popularityMap[serviceName] = mapOf(
                "rating" to avgRating,
                "reviews" to reviewCount
            )
        }

        return popularityMap
    }
}

