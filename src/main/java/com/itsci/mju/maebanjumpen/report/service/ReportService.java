package com.itsci.mju.maebanjumpen.report.service;

import com.itsci.mju.maebanjumpen.report.dto.ReportDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ReportService {
    List<ReportDTO> getAllReports();
    @Transactional
        // @Transactional สำคัญมากสำหรับเมธอดนี้
    ReportDTO createReport(ReportDTO reportDto);
    ReportDTO getReportById(int id);
    void deleteReport(int id);
    List<ReportDTO> getReportsByStatus(String reportStatus);
    ReportDTO updateReport(int id, ReportDTO reportDto);
    Optional<ReportDTO> findByPenaltyId(Integer penaltyId);
    Optional<ReportDTO> findLatestReportWithPenaltyByPersonId(Integer personId); // ยังคงเหมือนเดิม
    Optional<ReportDTO> findByHireIdAndReporterId(Integer hireId, Integer reporterId);
    void updateUserAccountStatus(int personId, boolean isBanned);

}
