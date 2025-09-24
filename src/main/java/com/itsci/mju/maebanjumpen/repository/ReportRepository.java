package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByReportStatus(String reportStatus);

    Optional<Report> findByPenalty_PenaltyId(Integer penaltyId);

    @Query("SELECT r FROM Report r " +
            "LEFT JOIN r.hirer h " +         // Join Report with Hirer
            "LEFT JOIN h.person hp " +       // Join Hirer with Person (for Hirer)
            "LEFT JOIN r.housekeeper hk " +  // Join Report with Housekeeper
            "LEFT JOIN hk.person hkp " +     // Join Housekeeper with Person (for Housekeeper)
            "WHERE (hp.personId = :personId OR hkp.personId = :personId) " + // ใช้ personId จาก Person object
            "AND r.penalty IS NOT NULL " +   // ต้องมี penalty เชื่อมโยงอยู่
            "ORDER BY r.reportDate DESC")    // เรียงตามวันที่ล่าสุด
    List<Report> findReportsWithPenaltyByPersonId(@Param("personId") Integer personId);

    Optional<Report> findByHire(Hire hire);
}