// src/main/java/com/itsci/mju/maebanjumpen/repository/ReviewRepository.java
package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
//public interface ReviewRepository extends JpaRepository<Review, Integer> {
//
//    // ตรวจสอบให้แน่ใจว่า attributePaths ครอบคลุมถึงทุกระดับที่ HireSerializer ต้องการ
//    // สำหรับ findAll
//    @Override
//    @EntityGraph(attributePaths = {
//            "hire", // โหลด Hire
//            "hire.hirer", // โหลด Hirer ของ Hire
//            "hire.hirer.person", // โหลด Person ของ Hirer
//            "hire.hirer.person.login", // โหลด Login ของ Person ของ Hirer
//
//            "hire.housekeeper", // โหลด Housekeeper ของ Hire
//            "hire.housekeeper.person", // โหลด Person ของ Housekeeper
//            "hire.housekeeper.person.login", // โหลด Login ของ Person ของ Housekeeper
//            "hire.housekeeper.housekeeperSkills" // โหลด Skills ของ Housekeeper (ถ้ามี)
//    })
//    List<Review> findAll();
//
//    // สำหรับ findById
//    @Override
//    @EntityGraph(attributePaths = {
//            "hire",
//            "hire.hirer",
//            "hire.hirer.person",
//            "hire.hirer.person.login",
//
//            "hire.housekeeper",
//            "hire.housekeeper.person",
//            "hire.housekeeper.person.login",
//            "hire.housekeeper.housekeeperSkills"
//    })
//    Optional<Review> findById(Integer id);
//
//    // สำหรับ findByHire_HireId
//    @EntityGraph(attributePaths = {
//            "hire",
//            "hire.hirer",
//            "hire.hirer.person",
//            "hire.hirer.person.login",
//
//            "hire.housekeeper",
//            "hire.housekeeper.person",
//            "hire.housekeeper.person.login",
//            "hire.housekeeper.housekeeperSkills"
//    })
//    Review findByHire_HireId(Integer hireId);
//}

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