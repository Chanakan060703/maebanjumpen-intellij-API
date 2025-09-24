package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Hire;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HireRepository extends JpaRepository<Hire, Integer> {

    // ** แก้ไข: เพิ่ม JOIN FETCH สำหรับ progressionImageUrls เพื่อแก้ไข LazyInitializationException **
    @EntityGraph(value = "Hire.fullDetails", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT h FROM Hire h LEFT JOIN FETCH h.progressionImageUrls WHERE h.hireId = :id")
    Optional<Hire> fetchByIdWithAllDetails(@Param("id") Integer id);

    @Query("SELECT DISTINCT h FROM Hire h " +
            "JOIN FETCH h.hirer hr " +
            "JOIN FETCH hr.person hrp " +
            "LEFT JOIN FETCH hrp.login " +
            "LEFT JOIN FETCH hr.transactions " +
            "JOIN FETCH h.housekeeper hk " +
            "JOIN FETCH hk.person hkp " +
            "LEFT JOIN FETCH hkp.login " +
            "LEFT JOIN FETCH hk.housekeeperSkills hks " +
            "LEFT JOIN FETCH hks.skillType " +
            "LEFT JOIN FETCH hk.transactions " +
            "LEFT JOIN FETCH h.review r " +
            "LEFT JOIN FETCH h.progressionImageUrls " + // <-- เพิ่มบรรทัดนี้
            "WHERE hr.id = :hirerId")
    List<Hire> findByHirerIdWithDetails(@Param("hirerId") Integer hirerId);

    @Query("SELECT DISTINCT h FROM Hire h " +
            "JOIN FETCH h.hirer hr " +
            "JOIN FETCH hr.person hrp " +
            "LEFT JOIN FETCH hrp.login " +
            "LEFT JOIN FETCH hr.transactions " +
            "JOIN FETCH h.housekeeper hk " +
            "JOIN FETCH hk.person hkp " +
            "LEFT JOIN FETCH hkp.login " +
            "LEFT JOIN FETCH hk.housekeeperSkills hks " +
            "LEFT JOIN FETCH hks.skillType " +
            "LEFT JOIN FETCH hk.transactions " +
            "LEFT JOIN FETCH h.review r")
    List<Hire> findAllWithDetails();

    @Query("SELECT DISTINCT h FROM Hire h " +
            "JOIN FETCH h.hirer hr " +
            "JOIN FETCH hr.person hrp " +
            "LEFT JOIN FETCH hrp.login " +
            "LEFT JOIN FETCH hr.transactions " +
            "JOIN FETCH h.housekeeper hk " +
            "JOIN FETCH hk.person hkp " +
            "LEFT JOIN FETCH hkp.login " +
            "LEFT JOIN FETCH hk.housekeeperSkills hks " +
            "LEFT JOIN FETCH hks.skillType " +
            "LEFT JOIN FETCH hk.transactions " +
            "LEFT JOIN FETCH h.review r " +
            "WHERE hk.id = :housekeeperId")
    List<Hire> findByHousekeeperIdWithDetails(@Param("housekeeperId") Integer housekeeperId);
}