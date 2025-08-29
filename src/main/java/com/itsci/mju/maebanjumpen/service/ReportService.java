package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Report;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ReportService {
    List<Report> getAllReports();

    @Transactional
        // @Transactional สำคัญมากสำหรับเมธอดนี้
    Report createReport(Report report);

    Report getReportById(int id);
    void deleteReport(int id);
    List<Report> getReportsByStatus(String reportStatus);
    Report updateReport(int id, Report report);

    Optional<Report> findByPenaltyId(Integer penaltyId);
    Optional<Report> findLatestReportWithPenaltyByPersonId(Integer personId); // ยังคงเหมือนเดิม


}