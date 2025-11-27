package com.itsci.mju.maebanjumpen.other.service.impl;

import com.itsci.mju.maebanjumpen.other.repository.ServicePopularityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServicePopularityService {

    @Autowired
    private ServicePopularityRepository servicePopularityRepository;

    /**
     * Retrieves aggregated popularity data for all service types.
     * This includes the average rating and the total number of reviews for each service.
     *
     * @return A Map where keys are service names (String) and values are another Map
     * containing "rating" (Double) and "reviews" (Integer).
     */
    @Transactional(readOnly = true)
    public Map<String, Map<String, Object>> getServicePopularity() {
        List<Object[]> results = servicePopularityRepository.findServicePopularityData();
        Map<String, Map<String, Object>> popularityMap = new HashMap<>();

        for (Object[] row : results) {
            String skillTypeName = (String) row[0];
            Double averageRating = (Double) row[1];
            Long reviewCount = (Long) row[2];

            Map<String, Object> details = new HashMap<>();
            // Ensure rating is not null, default to 0.0 if it is (e.g., no reviews yet)
            details.put("rating", averageRating != null ? averageRating : 0.0);
            // Ensure review count is not null, default to 0 if it is
            details.put("reviews", reviewCount != null ? reviewCount.intValue() : 0);
            popularityMap.put(skillTypeName, details);
        }
        return popularityMap;
    }
}
