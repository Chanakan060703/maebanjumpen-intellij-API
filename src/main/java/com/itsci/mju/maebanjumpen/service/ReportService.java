package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.ReportDTO;
import com.itsci.mju.maebanjumpen.model.Report;
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

    // ✅ เพิ่มเมธอดสำหรับค้นหารายงานโดย Hire ID และ Reporter ID
    Optional<ReportDTO> findByHireIdAndReporterId(Integer hireId, Integer reporterId);

    // ✅ [ใหม่] เมธอดสำหรับอัปเดตสถานะบัญชี (เรียกใช้ PersonService ภายใน)
    void updateUserAccountStatus(int personId, boolean isBanned);

}
