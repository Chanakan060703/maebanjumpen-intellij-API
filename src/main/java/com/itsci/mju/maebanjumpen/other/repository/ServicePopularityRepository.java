package com.itsci.mju.maebanjumpen.other.repository;

import com.itsci.mju.maebanjumpen.entity.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePopularityRepository extends JpaRepository<SkillType, Integer> {

    /**
     * Retrieves popularity data for each skill type, including average rating and total review count.
     * The query joins SkillType, HousekeeperSkill, Housekeeper, Hire, and Review
     * to aggregate review scores for completed hires associated with each skill type.
     *
     * @return A List of Object arrays, where each array contains:
     * - [0] String: skillTypeName
     * - [1] Double: average rating for that skill type
     * - [2] Long: total count of reviews for that skill type
     */
    @Query("SELECT st.skillTypeName, AVG(r.score), COUNT(r.reviewId) " +
            "FROM SkillType st " +
            "JOIN HousekeeperSkill hsk ON st.skillTypeId = hsk.skillType.skillTypeId " +
            "JOIN hsk.housekeeper hk " + // Simplified join for Housekeeper
            "JOIN Hire h ON hk.id = h.housekeeper.id " + // Join Hire through Housekeeper
            "LEFT JOIN Review r ON h.hireId = r.hire.hireId " + // Left join Review to include skills even without reviews
            "WHERE h.jobStatus = 'Completed' AND r.score IS NOT NULL " + // Only consider completed hires with reviews
            "GROUP BY st.skillTypeName")
    List<Object[]> findServicePopularityData();
}
