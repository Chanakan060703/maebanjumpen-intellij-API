package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.ReportDTO;
import com.itsci.mju.maebanjumpen.exception.AlreadyReportedException;
import com.itsci.mju.maebanjumpen.mapper.ReportMapper;
import com.itsci.mju.maebanjumpen.model.*;
import com.itsci.mju.maebanjumpen.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PartyRoleRepository partyRoleRepository;
    private final HirerRepository hirerRepository;
    private final HousekeeperRepository housekeeperRepository;
    private final PenaltyRepository penaltyRepository;
    private final HireRepository hireRepository;
    private final ReportMapper reportMapper;
    private final PersonService personService; // ✅ [ใหม่] เพิ่ม PersonService เพื่อใช้ในการจัดการสถานะบัญชี

    private void initializeReport(Report report) {
        if (report == null) return;
        Hibernate.initialize(report.getReporter());
        Hibernate.initialize(report.getHirer());
        Hibernate.initialize(report.getHousekeeper());
        Hibernate.initialize(report.getPenalty());
        Hibernate.initialize(report.getHire());

        if (report.getHirer() != null && report.getHirer().getPerson() != null) {
            Hibernate.initialize(report.getHirer().getPerson());
        }
        if (report.getHousekeeper() != null && report.getHousekeeper().getPerson() != null) {
            Hibernate.initialize(report.getHousekeeper().getPerson());
        }
        if (report.getReporter() != null && report.getReporter().getPerson() != null) {
            Hibernate.initialize(report.getReporter().getPerson());
        }
    }

    @Override
    public List<ReportDTO> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        reports.forEach(this::initializeReport);
        return reportMapper.toDtoList(reports);
    }

    @Override
    public ReportDTO getReportById(int id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
        initializeReport(report);
        return reportMapper.toDto(report);
    }

    @Override
    @Transactional
    public ReportDTO createReport(ReportDTO reportDto) {
        // 1. ตรวจสอบว่ามีการรายงานซ้ำหรือไม่ (ต้องดึง Hire Entity มาเทียบ)
        if (reportDto.getHireId() == null) {
            throw new IllegalArgumentException("Hire ID is required.");
        }

        // 🎯 ดึง ID จาก Object ที่ Client ส่งมา
        Integer reporterId = reportDto.getReporter() != null ? reportDto.getReporter().getId() : null;
        Integer hirerId = reportDto.getHirer() != null ? reportDto.getHirer().getId() : null;
        Integer housekeeperId = reportDto.getHousekeeper() != null ? reportDto.getHousekeeper().getId() : null;
        Integer penaltyId = reportDto.getPenalty() != null ? reportDto.getPenalty().getPenaltyId() : null;

        if (reporterId == null) {
            throw new IllegalArgumentException("Reporter ID is required in the reporter object.");
        }

        // ✅ [จุดแก้ไข] ตรวจสอบความซ้ำซ้อน: ต้องซ้ำทั้ง Hire ID และ Reporter ID (ผู้รายงานคนเดียวกัน)
        // Note: As the ReportRepository file is not provided, we assume the JPA method
        // findByHire_HireIdAndReporter_Id exists, which corresponds to the SQL:
        // WHERE r.hire.hireId = ? AND r.reporter.id = ?
        if (reportRepository.findByHire_HireIdAndReporter_Id(reportDto.getHireId(), reporterId).isPresent()) {
            // เปลี่ยนข้อความแจ้งเตือนให้ชัดเจนขึ้นสำหรับกรณีที่ผู้รายงานคนเดียวกันส่งซ้ำ
            throw new AlreadyReportedException("You have already submitted a report for this job.");
        }

        // 2. ดึง Entity จาก ID เพื่อผูกความสัมพันธ์
        Report report = reportMapper.toEntity(reportDto);

        PartyRole existingReporter = partyRoleRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found with ID: " + reporterId));

        Hire existingHire = hireRepository.findById(reportDto.getHireId())
                .orElseThrow(() -> new IllegalArgumentException("Hire not found with ID: " + reportDto.getHireId()));

        // 3. ผูกความสัมพันธ์
        report.setReporter(existingReporter);
        report.setHire(existingHire); // ✅ ผูก Hire Entity

        // 🚨 ใช้ ID ที่ดึงมาเพื่อดึง Entity ที่สมบูรณ์
        report.setHirer(hirerId != null ? hirerRepository.findById(hirerId).orElse(null) : null);
        report.setHousekeeper(housekeeperId != null ? housekeeperRepository.findById(housekeeperId).orElse(null) : null);
        report.setPenalty(penaltyId != null ? penaltyRepository.findById(penaltyId).orElse(null) : null);

        // 4. Set default status
        if (report.getReportStatus() == null || report.getReportStatus().isEmpty()) {
            report.setReportStatus("pending");
        }

        Report savedReport = reportRepository.save(report);
        initializeReport(savedReport);
        return reportMapper.toDto(savedReport);
    }


    @Override
    @Transactional
    public ReportDTO updateReport(int id, ReportDTO reportDto) {
        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        // 1. อัปเดต basic fields
        if (reportDto.getReportTitle() != null) existingReport.setReportTitle(reportDto.getReportTitle());
        if (reportDto.getReportMessage() != null) existingReport.setReportMessage(reportDto.getReportMessage());
        if (reportDto.getReportDate() != null) existingReport.setReportDate(reportDto.getReportDate());
        if (reportDto.getReportStatus() != null) existingReport.setReportStatus(reportDto.getReportStatus());

        // 2. อัปเดตความสัมพันธ์โดยใช้ Object จาก DTO

        // 🚨 ดึง ID จาก Object
        Integer newReporterId = reportDto.getReporter() != null ? reportDto.getReporter().getId() : null;
        Integer newHirerId = reportDto.getHirer() != null ? reportDto.getHirer().getId() : null;
        Integer newHousekeeperId = reportDto.getHousekeeper() != null ? reportDto.getHousekeeper().getId() : null;
        Integer newPenaltyId = reportDto.getPenalty() != null ? reportDto.getPenalty().getPenaltyId() : null;


        if (newReporterId != null) {
            PartyRole newReporter = partyRoleRepository.findById(newReporterId)
                    .orElseThrow(() -> new IllegalArgumentException("Reporter not found with ID: " + newReporterId));
            existingReport.setReporter(newReporter);
        }

        if (newHirerId != null) {
            Hirer newHirer = hirerRepository.findById(newHirerId)
                    .orElseThrow(() -> new IllegalArgumentException("Hirer not found with ID: " + newHirerId));
            existingReport.setHirer(newHirer);
        }

        if (newHousekeeperId != null) {
            Housekeeper newHousekeeper = housekeeperRepository.findById(newHousekeeperId)
                    .orElseThrow(() -> new IllegalArgumentException("Housekeeper not found with ID: " + newHousekeeperId));
            existingReport.setHousekeeper(newHousekeeper);
        }

        if (newPenaltyId != null) {
            Penalty newPenalty = penaltyRepository.findById(newPenaltyId)
                    .orElseThrow(() -> new IllegalArgumentException("Penalty not found with ID: " + newPenaltyId));
            existingReport.setPenalty(newPenalty);
        } else if (reportDto.getPenalty() == null) { // ตรวจสอบว่า client ส่ง null มาหรือไม่
            existingReport.setPenalty(null); // อนุญาตให้ล้าง Penalty ได้
        }

        Report savedReport = reportRepository.save(existingReport);
        initializeReport(savedReport);
        return reportMapper.toDto(savedReport);
    }

    @Override
    @Transactional
    public void deleteReport(int id) {
        reportRepository.deleteById(id);
    }

    @Override
    public List<ReportDTO> getReportsByStatus(String reportStatus) {
        List<Report> reports = reportRepository.findByReportStatus(reportStatus);
        reports.forEach(this::initializeReport);
        return reportMapper.toDtoList(reports);
    }

    @Override
    public Optional<ReportDTO> findByPenaltyId(Integer penaltyId) {
        if (penaltyId == null) {
            return Optional.empty();
        }
        Optional<Report> reportOptional = reportRepository.findByPenalty_PenaltyId(penaltyId);
        reportOptional.ifPresent(this::initializeReport);

        return reportOptional.map(reportMapper::toDto);
    }

    @Override
    public Optional<ReportDTO> findLatestReportWithPenaltyByPersonId(Integer personId) {
        // [Note: Assuming findReportsWithPenaltyByPersonId is a repository method]
        List<Report> reports = reportRepository.findReportsWithPenaltyByPersonId(personId);
        if (!reports.isEmpty()) {
            Report latestReport = reports.get(0);
            initializeReport(latestReport);
            return Optional.of(reportMapper.toDto(latestReport));
        }
        return Optional.empty();
    }

    // ✅ เพิ่ม implementation ของเมธอดใหม่
    @Override
    public Optional<ReportDTO> findByHireIdAndReporterId(Integer hireId, Integer reporterId) {
        if (hireId == null || reporterId == null) {
            return Optional.empty();
        }
        // As a placeholder, we assume the repository method exists to find by both IDs
        Optional<Report> reportOptional = reportRepository.findByHire_HireIdAndReporter_Id(hireId, reporterId);
        reportOptional.ifPresent(this::initializeReport);
        return reportOptional.map(reportMapper::toDto);
    }

    /**
     * ✅ [ใหม่] เมธอดสำหรับอัปเดตสถานะบัญชีของผู้ใช้ (ban/unban)
     * โดยส่งต่อการทำงานไปยัง PersonService
     */
    @Override
    @Transactional
    public void updateUserAccountStatus(int personId, boolean isBanned) {
        // แปลง boolean เป็น String สถานะ
        String newStatus = isBanned ? "banned" : "active";

        // เรียกใช้ PersonService ที่มีอยู่แล้วเพื่ออัปเดตสถานะจริงในตาราง Person
        personService.updateAccountStatus(personId, newStatus);
    }
}
