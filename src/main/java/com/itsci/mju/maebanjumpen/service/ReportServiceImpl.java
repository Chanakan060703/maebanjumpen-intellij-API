package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.ReportDTO; // ⬅️ ต้องใช้ DTO
import com.itsci.mju.maebanjumpen.exception.AlreadyReportedException;
import com.itsci.mju.maebanjumpen.mapper.ReportMapper; // ⬅️ ต้องใช้ Mapper
import com.itsci.mju.maebanjumpen.model.*;
import com.itsci.mju.maebanjumpen.repository.*;
import lombok.RequiredArgsConstructor; // ⬅️ ใช้ Lombok แทน @Autowired
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // สำหรับ stream

@Service
@RequiredArgsConstructor // ⬅️ ใช้ Constructor Injection
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PartyRoleRepository partyRoleRepository;
    private final HirerRepository hirerRepository;
    private final HousekeeperRepository housekeeperRepository;
    private final PenaltyRepository penaltyRepository;
    private final HireRepository hireRepository; // ✅ เพิ่ม HireRepository
    private final ReportMapper reportMapper;

    private void initializeReport(Report report) {
        if (report == null) return;
        Hibernate.initialize(report.getReporter());
        Hibernate.initialize(report.getHirer());
        Hibernate.initialize(report.getHousekeeper());
        Hibernate.initialize(report.getPenalty());
        Hibernate.initialize(report.getHire()); // ✅ Initialize Hire

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
        if (reportRepository.findByHire_HireId(reportDto.getHireId()).isPresent()) {
            throw new AlreadyReportedException("This job has already been reported.");
        }

        // 2. ดึง Entity จาก ID เพื่อผูกความสัมพันธ์
        Report report = reportMapper.toEntity(reportDto);
        PartyRole existingReporter = partyRoleRepository.findById(reportDto.getReporterId())
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found with ID: " + reportDto.getReporterId()));

        Hire existingHire = hireRepository.findById(reportDto.getHireId())
                .orElseThrow(() -> new IllegalArgumentException("Hire not found with ID: " + reportDto.getHireId()));

        // 3. ผูกความสัมพันธ์
        report.setReporter(existingReporter);
        report.setHire(existingHire); // ✅ ผูก Hire Entity
        report.setHirer(reportDto.getHirerId() != null ? hirerRepository.findById(reportDto.getHirerId()).orElse(null) : null);
        report.setHousekeeper(reportDto.getHousekeeperId() != null ? housekeeperRepository.findById(reportDto.getHousekeeperId()).orElse(null) : null);
        report.setPenalty(reportDto.getPenaltyId() != null ? penaltyRepository.findById(reportDto.getPenaltyId()).orElse(null) : null);

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
    public ReportDTO updateReport(int id, ReportDTO reportDto) { // ⬅️ เปลี่ยน Input/Output เป็น DTO
        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        // 1. อัปเดต basic fields
        if (reportDto.getReportTitle() != null) existingReport.setReportTitle(reportDto.getReportTitle());
        if (reportDto.getReportMessage() != null) existingReport.setReportMessage(reportDto.getReportMessage());
        if (reportDto.getReportDate() != null) existingReport.setReportDate(reportDto.getReportDate());
        if (reportDto.getReportStatus() != null) existingReport.setReportStatus(reportDto.getReportStatus());

        // 2. อัปเดตความสัมพันธ์โดยใช้ ID จาก DTO
        if (reportDto.getReporterId() != null) {
            PartyRole newReporter = partyRoleRepository.findById(reportDto.getReporterId())
                    .orElseThrow(() -> new IllegalArgumentException("Reporter not found with ID: " + reportDto.getReporterId()));
            existingReport.setReporter(newReporter);
        }

        if (reportDto.getHirerId() != null) {
            Hirer newHirer = hirerRepository.findById(reportDto.getHirerId())
                    .orElseThrow(() -> new IllegalArgumentException("Hirer not found with ID: " + reportDto.getHirerId()));
            existingReport.setHirer(newHirer);
        }

        if (reportDto.getHousekeeperId() != null) {
            Housekeeper newHousekeeper = housekeeperRepository.findById(reportDto.getHousekeeperId())
                    .orElseThrow(() -> new IllegalArgumentException("Housekeeper not found with ID: " + reportDto.getHousekeeperId()));
            existingReport.setHousekeeper(newHousekeeper);
        }

        if (reportDto.getPenaltyId() != null) {
            Penalty newPenalty = penaltyRepository.findById(reportDto.getPenaltyId())
                    .orElseThrow(() -> new IllegalArgumentException("Penalty not found with ID: " + reportDto.getPenaltyId()));
            existingReport.setPenalty(newPenalty);
        } else if (reportDto.getPenaltyId() == null) {
            existingReport.setPenalty(null); // อนุญาตให้ล้าง Penalty ได้
        }

        Report savedReport = reportRepository.save(existingReport);
        initializeReport(savedReport);
        return reportMapper.toDto(savedReport); // ⬅️ แปลง Entity กลับเป็น DTO
    }

    @Override
    @Transactional
    public void deleteReport(int id) {
        reportRepository.deleteById(id);
    }

    @Override
    public List<ReportDTO> getReportsByStatus(String reportStatus) { // ⬅️ เปลี่ยน Output เป็น DTO
        List<Report> reports = reportRepository.findByReportStatus(reportStatus);
        reports.forEach(this::initializeReport);
        return reportMapper.toDtoList(reports); // ⬅️ ใช้ Mapper
    }

    @Override
    public Optional<ReportDTO> findByPenaltyId(Integer penaltyId) { // ⬅️ เปลี่ยน Output เป็น DTO
        if (penaltyId == null) {
            return Optional.empty();
        }
        Optional<Report> reportOptional = reportRepository.findByPenalty_PenaltyId(penaltyId);
        reportOptional.ifPresent(this::initializeReport);

        return reportOptional.map(reportMapper::toDto); // ⬅️ แปลง Entity เป็น DTO
    }

    @Override
    public Optional<ReportDTO> findLatestReportWithPenaltyByPersonId(Integer personId) { // ⬅️ เปลี่ยน Output เป็น DTO
        List<Report> reports = reportRepository.findReportsWithPenaltyByPersonId(personId);
        if (!reports.isEmpty()) {
            Report latestReport = reports.get(0);
            initializeReport(latestReport);
            return Optional.of(reportMapper.toDto(latestReport)); // ⬅️ แปลง Entity เป็น DTO
        }
        return Optional.empty();
    }
}