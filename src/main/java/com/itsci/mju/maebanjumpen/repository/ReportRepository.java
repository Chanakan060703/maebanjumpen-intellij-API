package com.itsci.mju.maebanjumpen.repository;

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
            "LEFT JOIN r.hirer h " +
            "LEFT JOIN h.person hp " +
            "LEFT JOIN r.housekeeper hk " +
            "LEFT JOIN hk.person hkp " +
            "WHERE (hp.personId = :personId OR hkp.personId = :personId) " +
            "AND r.penalty IS NOT NULL " +
            "ORDER BY r.reportDate DESC")
    List<Report> findReportsWithPenaltyByPersonId(@Param("personId") Integer personId);

    // 🛑 แก้ไข: จาก Optional<Report> เป็น List<Report> เพราะ Hire สามารถมีได้หลาย Report (ManyToOne)
    List<Report> findByHire_HireId(Integer hireId);

    Optional<Report> findByHire_HireIdAndReporter_Id(Integer hireId, Integer reporterId);
}
