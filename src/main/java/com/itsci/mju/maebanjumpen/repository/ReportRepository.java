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

    /**
     * Finds reports with an associated penalty for a person (either hirer or housekeeper).
     * The join now goes through the 'hire' relationship to find the associated hirer/housekeeper.
     */
    @Query("SELECT r FROM Report r " +
            "JOIN r.hire h " + // 🎯 Join ผ่าน Hire
            "LEFT JOIN h.hirer hr " + // 🎯 ดึง Hirer จาก Hire
            "LEFT JOIN hr.person hrp " + // ดึง Person ของ Hirer
            "LEFT JOIN h.housekeeper hk " + // 🎯 ดึง Housekeeper จาก Hire
            "LEFT JOIN hk.person hkp " + // ดึง Person ของ Housekeeper
            "WHERE (hrp.personId = :personId OR hkp.personId = :personId) " +
            "AND r.penalty IS NOT NULL " +
            "ORDER BY r.reportDate DESC")
    List<Report> findReportsWithPenaltyByPersonId(@Param("personId") Integer personId);

    // 🛑 แก้ไข: จาก Optional<Report> เป็น List<Report> เพราะ Hire สามารถมีได้หลาย Report (ManyToOne)
    List<Report> findByHire_HireId(Integer hireId);

    Optional<Report> findByHire_HireIdAndReporter_Id(Integer hireId, Integer reporterId);
}