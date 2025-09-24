package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.exception.AlreadyReportedException;
import com.itsci.mju.maebanjumpen.model.*;
import com.itsci.mju.maebanjumpen.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PartyRoleRepository partyRoleRepository;
    private final HirerRepository hirerRepository;
    private final HousekeeperRepository housekeeperRepository;
    private final PenaltyRepository penaltyRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository,
                             PartyRoleRepository partyRoleRepository,
                             HirerRepository hirerRepository,
                             HousekeeperRepository housekeeperRepository,
                             PenaltyRepository penaltyRepository) {
        this.reportRepository = reportRepository;
        this.partyRoleRepository = partyRoleRepository;
        this.hirerRepository = hirerRepository;
        this.housekeeperRepository = housekeeperRepository;
        this.penaltyRepository = penaltyRepository;
    }

    // Helper method to initialize report properties
    private void initializeReport(Report report) {
        if (report == null) return;

        Hibernate.initialize(report.getReporter());
        Hibernate.initialize(report.getHirer());
        Hibernate.initialize(report.getHousekeeper());
        Hibernate.initialize(report.getPenalty());

        // Initialize nested properties for Hirer
        if (report.getHirer() instanceof Hirer) {
            Hirer hirer = (Hirer) report.getHirer();
            if (hirer.getPerson() != null) {
                Hibernate.initialize(hirer.getPerson());
                if (hirer.getPerson().getLogin() != null) {
                    Hibernate.initialize(hirer.getPerson().getLogin());
                }
            }
            if (hirer.getHires() != null) {
                Hibernate.initialize(hirer.getHires());
                hirer.getHires().forEach(hire -> {
                    Hibernate.initialize(hire.getReview());
                    Hibernate.initialize(hire.getHousekeeper());
                    if (hire.getHousekeeper() != null && hire.getHousekeeper().getPerson() != null) {
                        Hibernate.initialize(hire.getHousekeeper().getPerson());
                        if (hire.getHousekeeper().getPerson().getLogin() != null) {
                            Hibernate.initialize(hire.getHousekeeper().getPerson().getLogin());
                        }
                    }
                });
            }
        }

        // Initialize nested properties for Housekeeper
        if (report.getHousekeeper() instanceof Housekeeper) {
            Housekeeper housekeeper = (Housekeeper) report.getHousekeeper();
            if (housekeeper.getPerson() != null) {
                Hibernate.initialize(housekeeper.getPerson());
                if (housekeeper.getPerson().getLogin() != null) {
                    Hibernate.initialize(housekeeper.getPerson().getLogin());
                }
            }
            if (housekeeper.getHousekeeperSkills() != null) {
                Hibernate.initialize(housekeeper.getHousekeeperSkills());
                housekeeper.getHousekeeperSkills().forEach(skill -> Hibernate.initialize(skill.getSkillType()));
            }
            if (housekeeper.getHires() != null) {
                Hibernate.initialize(housekeeper.getHires());
                housekeeper.getHires().forEach(hire -> {
                    Hibernate.initialize(hire.getReview());
                    Hibernate.initialize(hire.getHirer());
                    if (hire.getHirer() != null && hire.getHirer().getPerson() != null) {
                        Hibernate.initialize(hire.getHirer().getPerson());
                        if (hire.getHirer().getPerson().getLogin() != null) {
                            Hibernate.initialize(hire.getHirer().getPerson().getLogin());
                        }
                    }
                });
            }
        }

        // Initialize nested properties for Reporter
        if (report.getReporter() != null && report.getReporter().getPerson() != null) {
            Hibernate.initialize(report.getReporter().getPerson());
            if (report.getReporter().getPerson().getLogin() != null) {
                Hibernate.initialize(report.getReporter().getPerson().getLogin());
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        reports.forEach(this::initializeReport);
        return reports;
    }


    @Override
    @Transactional(readOnly = true)
    public Report getReportById(int id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
        initializeReport(report);
        return report;
    }


    @Override
    @Transactional
    public Report createReport(Report report) {
        if (report.getReporter() == null || report.getReporter().getId() == null) {
            throw new IllegalArgumentException("Reporter is required and must have a valid ID.");
        }

        PartyRole existingReporter = partyRoleRepository.findById(report.getReporter().getId())
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found with ID: " + report.getReporter().getId()));

        if (report.getHire() == null) {
            throw new IllegalArgumentException("Hire information is required.");
        }

        Optional<Report> existingReportForHire = reportRepository.findByHire(report.getHire());
        if (existingReportForHire.isPresent()) {
            // แก้ไขบรรทัดนี้: โยน exception ที่กำหนดเอง
            throw new AlreadyReportedException("This job has already been reported.");
        }

        // Set Reporter, Hirer, and Housekeeper from database
        report.setReporter(existingReporter);

        // Use the Hirer from the report object if provided, otherwise check if reporter is a hirer
        if (report.getHirer() != null && report.getHirer().getId() != null) {
            Hirer existingHirer = hirerRepository.findById(report.getHirer().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Hirer not found with ID: " + report.getHirer().getId()));
            report.setHirer(existingHirer);
        } else if (existingReporter instanceof Hirer) {
            report.setHirer((Hirer) existingReporter);
        } else {
            throw new IllegalArgumentException("Hirer is required and must have a valid ID.");
        }

        // Validate and set Housekeeper (optional)
        if (report.getHousekeeper() != null && report.getHousekeeper().getId() != null) {
            Housekeeper existingHousekeeper = housekeeperRepository.findById(report.getHousekeeper().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Housekeeper not found with ID: " + report.getHousekeeper().getId()));
            report.setHousekeeper(existingHousekeeper);
        } else {
            report.setHousekeeper(null);
        }

        // Validate and set Penalty (optional)
        if (report.getPenalty() != null && report.getPenalty().getPenaltyId() != null) {
            Penalty existingPenalty = penaltyRepository.findById(report.getPenalty().getPenaltyId())
                    .orElseThrow(() -> new IllegalArgumentException("Penalty not found with ID: " + report.getPenalty().getPenaltyId()));
            report.setPenalty(existingPenalty);
        } else {
            report.setPenalty(null);
        }

        // Set default report status if not provided
        if (report.getReportStatus() == null || report.getReportStatus().isEmpty()) {
            report.setReportStatus("pending");
        }

        Report savedReport = reportRepository.save(report);
        initializeReport(savedReport); // Initialize the saved report before returning
        return savedReport;
    }

    @Override
    @Transactional
    public Report updateReport(int id, Report updatedReport) {
        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        // Update basic fields
        existingReport.setReportTitle(updatedReport.getReportTitle());
        existingReport.setReportMessage(updatedReport.getReportMessage());
        existingReport.setReportDate(updatedReport.getReportDate());
        existingReport.setReportStatus(updatedReport.getReportStatus());

        // Update Reporter
        if (updatedReport.getReporter() != null && updatedReport.getReporter().getId() != null) {
            PartyRole newReporter = partyRoleRepository.findById(updatedReport.getReporter().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Reporter not found with ID: " + updatedReport.getReporter().getId()));
            existingReport.setReporter(newReporter);
        } else {
            existingReport.setReporter(null);
        }

        // Update Hirer
        if (updatedReport.getHirer() != null && updatedReport.getHirer().getId() != null) {
            Hirer newHirer = hirerRepository.findById(updatedReport.getHirer().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Hirer not found with ID: " + updatedReport.getHirer().getId()));
            existingReport.setHirer(newHirer);
        } else {
            existingReport.setHirer(null);
        }

        // Update Housekeeper
        if (updatedReport.getHousekeeper() != null && updatedReport.getHousekeeper().getId() != null) {
            Housekeeper newHousekeeper = housekeeperRepository.findById(updatedReport.getHousekeeper().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Housekeeper not found with ID: " + updatedReport.getHousekeeper().getId()));
            existingReport.setHousekeeper(newHousekeeper);
        } else {
            existingReport.setHousekeeper(null);
        }

        // Update Penalty
        if (updatedReport.getPenalty() != null && updatedReport.getPenalty().getPenaltyId() != null) {
            Penalty newPenalty = penaltyRepository.findById(updatedReport.getPenalty().getPenaltyId())
                    .orElseThrow(() -> new IllegalArgumentException("Penalty not found with ID: " + updatedReport.getPenalty().getPenaltyId()));
            existingReport.setPenalty(newPenalty);
        } else {
            existingReport.setPenalty(null);
        }

        Report savedReport = reportRepository.save(existingReport);
        initializeReport(savedReport);
        return savedReport;
    }

    @Override
    @Transactional
    public void deleteReport(int id) {
        reportRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getReportsByStatus(String reportStatus) {
        List<Report> reports = reportRepository.findByReportStatus(reportStatus);
        reports.forEach(this::initializeReport);
        return reports;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> findByPenaltyId(Integer penaltyId) {
        if (penaltyId == null) {
            return Optional.empty();
        }
        Optional<Report> reportOptional = reportRepository.findByPenalty_PenaltyId(penaltyId);
        reportOptional.ifPresent(this::initializeReport);
        return reportOptional;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> findLatestReportWithPenaltyByPersonId(Integer personId) {
        List<Report> reports = reportRepository.findReportsWithPenaltyByPersonId(personId);
        if (!reports.isEmpty()) {
            Report latestReport = reports.get(0);
            initializeReport(latestReport);
            return Optional.of(latestReport);
        }
        return Optional.empty();
    }
}