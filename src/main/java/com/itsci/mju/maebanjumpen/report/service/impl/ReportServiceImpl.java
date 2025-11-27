package com.itsci.mju.maebanjumpen.report.service.impl;

import com.itsci.mju.maebanjumpen.hire.repository.HireRepository;
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository;
import com.itsci.mju.maebanjumpen.penalty.repository.PenaltyRepository;
import com.itsci.mju.maebanjumpen.report.dto.ReportDTO;
import com.itsci.mju.maebanjumpen.exception.AlreadyReportedException;
import com.itsci.mju.maebanjumpen.mapper.ReportMapper;
import com.itsci.mju.maebanjumpen.entity.*;
import com.itsci.mju.maebanjumpen.report.repository.ReportRepository;
import com.itsci.mju.maebanjumpen.hire.service.impl.HireStatusUpdateService;
import com.itsci.mju.maebanjumpen.person.service.PersonService;
import com.itsci.mju.maebanjumpen.report.service.ReportService;
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
    // ❌ ลบ private final HirerRepository hirerRepository;
    // ❌ ลบ private final HousekeeperRepository housekeeperRepository;
    private final PenaltyRepository penaltyRepository;
    private final HireRepository hireRepository;
    private final ReportMapper reportMapper;
    private final PersonService personService;

    // 🎯 Service สำหรับการจัดการการเปลี่ยนสถานะตามเวลา
    private final HireStatusUpdateService hireStatusUpdateService;

    /**
     * Initializes lazy-loaded collections/associations of the Report entity.
     * Hirer and Housekeeper are now initialized via the Hire object.
     */
    private void initializeReport(Report report) {
        if (report == null) return;
        Hibernate.initialize(report.getReporter());
        // ❌ ลบ: report.getHirer() และ report.getHousekeeper() ถูกลบออกจาก Report Entity แล้ว

        Hibernate.initialize(report.getPenalty());
        Hibernate.initialize(report.getHire());

        // 💡 ตรวจสอบและ Initialize Hirer/Housekeeper ผ่าน Hire object
        if (report.getHire() != null) {
            Hibernate.initialize(report.getHire().getHirer());
            Hibernate.initialize(report.getHire().getHousekeeper());

            // Initialize Person (หากต้องการดึงชื่อ/ข้อมูลพื้นฐาน)
            if (report.getHire().getHirer() != null && report.getHire().getHirer().getPerson() != null) {
                Hibernate.initialize(report.getHire().getHirer().getPerson());
            }
            if (report.getHire().getHousekeeper() != null && report.getHire().getHousekeeper().getPerson() != null) {
                Hibernate.initialize(report.getHire().getHousekeeper().getPerson());
            }
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
        if (reportDto.getHire() == null || reportDto.getHire().getHireId() == null) {
            throw new IllegalArgumentException("Hire object with ID is required.");
        }

        Integer reporterId = reportDto.getReporter() != null ? reportDto.getReporter().getId() : null;
        Integer hireId = reportDto.getHire().getHireId(); // ✅ ดึง Hire ID จาก HireDTO
        Integer penaltyId = reportDto.getPenalty() != null ? reportDto.getPenalty().getPenaltyId() : null;

        if (reporterId == null) {
            throw new IllegalArgumentException("Reporter ID is required in the reporter object.");
        }

        // 🛑 ตรวจสอบการรายงานซ้ำ
        if (reportRepository.findByHire_HireIdAndReporter_Id(hireId, reporterId).isPresent()) {
            throw new AlreadyReportedException("You have already submitted a report for this job.");
        }

        Report report = reportMapper.toEntity(reportDto);

        PartyRole existingReporter = partyRoleRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found with ID: " + reporterId));

        Hire existingHire = hireRepository.findById(hireId)
                .orElseThrow(() -> new IllegalArgumentException("Hire not found with ID: " + hireId));

        // 2. ผูก Entity
        report.setReporter(existingReporter);
        report.setHire(existingHire);
        // ❌ ลบ: report.setHirer(...) และ report.setHousekeeper(...)
        report.setPenalty(penaltyId != null ? penaltyRepository.findById(penaltyId).orElse(null) : null);

        if (report.getReportStatus() == null || report.getReportStatus().isEmpty()) {
            report.setReportStatus("pending"); // สถานะของรายงาน
        }

        Report savedReport = reportRepository.save(report);

        // ----------------------------------------------------
        // ✅ LOGIC การเปลี่ยนสถานะงานจ้าง (อยู่ใน Transaction เดียวกัน)
        // ----------------------------------------------------

        // 3. เปลี่ยนสถานะงานจ้าง (Hire) เป็น "Reported" ทันที
        existingHire.setJobStatus("Reported");
        hireRepository.save(existingHire);

        System.out.println("LOG: Hire ID " + existingHire.getHireId() + " status set to 'Reported' and report saved.");


        // 4. กำหนดเวลาเปลี่ยนสถานะกลับเป็น "Completed" ใน 3 วินาที (Task Scheduler)
        if (existingHire.getHireId() != null) {
            hireStatusUpdateService.scheduleStatusRevert(
                    existingHire.getHireId(),
                    3L
            );
        }
        // ----------------------------------------------------

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

        // 2. อัปเดตความสัมพันธ์
        Integer newReporterId = reportDto.getReporter() != null ? reportDto.getReporter().getId() : null;
        Integer newPenaltyId = reportDto.getPenalty() != null ? reportDto.getPenalty().getPenaltyId() : null;

        // อัปเดต Hire (ถ้ามีการส่ง HireDTO มา)
        if (reportDto.getHire() != null && reportDto.getHire().getHireId() != null) {
            Hire newHire = hireRepository.findById(reportDto.getHire().getHireId())
                    .orElseThrow(() -> new IllegalArgumentException("Hire not found with ID: " + reportDto.getHire().getHireId()));
            existingReport.setHire(newHire);
        }

        if (newReporterId != null) {
            PartyRole newReporter = partyRoleRepository.findById(newReporterId)
                    .orElseThrow(() -> new IllegalArgumentException("Reporter not found with ID: " + newReporterId));
            existingReport.setReporter(newReporter);
        }

        // ❌ ลบ: ลอจิกการอัปเดต Hirer/Housekeeper โดยตรงออก

        if (newPenaltyId != null) {
            Penalty newPenalty = penaltyRepository.findById(newPenaltyId)
                    .orElseThrow(() -> new IllegalArgumentException("Penalty not found with ID: " + newPenaltyId));
            existingReport.setPenalty(newPenalty);
        } else if (reportDto.getPenalty() == null) {
            existingReport.setPenalty(null);
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

    @Override
    public Optional<ReportDTO> findByHireIdAndReporterId(Integer hireId, Integer reporterId) {
        if (hireId == null || reporterId == null) {
            return Optional.empty();
        }
        Optional<Report> reportOptional = reportRepository.findByHire_HireIdAndReporter_Id(hireId, reporterId);
        reportOptional.ifPresent(this::initializeReport);
        return reportOptional.map(reportMapper::toDto);
    }

    @Override
    @Transactional
    public void updateUserAccountStatus(int personId, boolean isBanned) {
        String newStatus = isBanned ? "banned" : "active";
        personService.updateAccountStatus(personId, newStatus);
    }
}