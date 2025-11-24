// src/main/java/com/itsci/mju/maebanjumpen/repository/ReviewRepository.java
package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    static final String REVIEW_GRAPH = "review-with-hire-details";

    @Override
    // ⭐️ FIX: ใช้ EntityGraph value
    @EntityGraph(value = REVIEW_GRAPH, type = EntityGraph.EntityGraphType.LOAD)
    List<Review> findAll();

    @Override
    // ⭐️ FIX: ใช้ EntityGraph value
    @EntityGraph(value = REVIEW_GRAPH, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Review> findById(Integer id);

    // ⭐️ FIX: ใช้ EntityGraph value
    @EntityGraph(value = REVIEW_GRAPH, type = EntityGraph.EntityGraphType.LOAD)
    Review findByHire_HireId(Integer hireId);
}