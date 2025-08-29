package com.itsci.mju.maebanjumpen.service;

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

    @Override
    @Transactional(readOnly = true) // เพิ่ม readOnly = true
    public List<Report> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        for (Report report : reports) {
            // Initialize direct relationships
            Hibernate.initialize(report.getReporter());
            Hibernate.initialize(report.getHirer());
            Hibernate.initialize(report.getHousekeeper());
            Hibernate.initialize(report.getPenalty());

            // Handle Hirer and its nested properties
            if (report.getHirer() != null) {
                // Ensure it's a Hirer instance before accessing specific methods like getHires()
                if (report.getHirer() instanceof Hirer) {
                    Hirer hirer = (Hirer) report.getHirer(); // Explicitly cast
                    Hibernate.initialize(hirer.getHires());
                    if (hirer.getHires() != null) {
                        for (Hire hire : hirer.getHires()) {
                            Hibernate.initialize(hire.getReview());
                            Hibernate.initialize(hire.getHousekeeper());
                            if (hire.getHousekeeper() != null && hire.getHousekeeper().getPerson() != null) {
                                Hibernate.initialize(hire.getHousekeeper().getPerson());
                                if (hire.getHousekeeper().getPerson().getLogin() != null) {
                                    Hibernate.initialize(hire.getHousekeeper().getPerson().getLogin());
                                }
                            }
                        }
                    }
                    if (hirer.getPerson() != null) {
                        Hibernate.initialize(hirer.getPerson());
                        if (hirer.getPerson().getLogin() != null) {
                            Hibernate.initialize(hirer.getPerson().getLogin());
                        }
                    }
                }
            }

            // Handle Housekeeper and its nested properties
            if (report.getHousekeeper() != null) {
                // Ensure it's a Housekeeper instance before accessing specific methods like getHires() or getHousekeeperSkills()
                if (report.getHousekeeper() instanceof Housekeeper) {
                    Housekeeper housekeeper = (Housekeeper) report.getHousekeeper(); // Explicitly cast
                    Hibernate.initialize(housekeeper.getHires());
                    if (housekeeper.getHires() != null) {
                        for (Hire hire : housekeeper.getHires()) {
                            Hibernate.initialize(hire.getReview());
                            Hibernate.initialize(hire.getHirer());
                            if (hire.getHirer() != null && hire.getHirer().getPerson() != null) {
                                Hibernate.initialize(hire.getHirer().getPerson());
                                if (hire.getHirer().getPerson().getLogin() != null) {
                                    Hibernate.initialize(hire.getHirer().getPerson().getLogin());
                                }
                            }
                        }
                    }
                    Hibernate.initialize(housekeeper.getHousekeeperSkills()); // <<< ตรงนี้สำคัญ: โหลด HousekeeperSkills
                    if (housekeeper.getHousekeeperSkills() != null) {
                        for (HousekeeperSkill skill : housekeeper.getHousekeeperSkills()) {
                            Hibernate.initialize(skill.getSkillType());
                        }
                    }
                    if (housekeeper.getPerson() != null) {
                        Hibernate.initialize(housekeeper.getPerson());
                        if (housekeeper.getPerson().getLogin() != null) {
                            Hibernate.initialize(housekeeper.getPerson().getLogin());
                        }
                    }
                }
            }

            // Handle Reporter and its person/login
            if (report.getReporter() != null) {
                // Note: Reporter is PartyRole, which also has a Person
                if (report.getReporter().getPerson() != null) {
                    Hibernate.initialize(report.getReporter().getPerson());
                    if (report.getReporter().getPerson().getLogin() != null) {
                        Hibernate.initialize(report.getReporter().getPerson().getLogin());
                    }
                }
            }
        }
        return reports;
    }

    @Override
    @Transactional(readOnly = true) // เพิ่ม readOnly = true
    public Report getReportById(int id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        // Initialize direct relationships
        Hibernate.initialize(report.getReporter());
        Hibernate.initialize(report.getHirer());
        Hibernate.initialize(report.getHousekeeper());
        Hibernate.initialize(report.getPenalty());

        // Handle Hirer and its nested properties
        if (report.getHirer() != null) {
            if (report.getHirer() instanceof Hirer) {
                Hirer hirer = (Hirer) report.getHirer(); // Explicitly cast
                Hibernate.initialize(hirer.getHires());
                if (hirer.getHires() != null) {
                    for (Hire hire : hirer.getHires()) {
                        Hibernate.initialize(hire.getReview());
                        Hibernate.initialize(hire.getHousekeeper());
                        if (hire.getHousekeeper() != null && hire.getHousekeeper().getPerson() != null) {
                            Hibernate.initialize(hire.getHousekeeper().getPerson());
                            if (hire.getHousekeeper().getPerson().getLogin() != null) {
                                Hibernate.initialize(hire.getHousekeeper().getPerson().getLogin());
                            }
                        }
                    }
                }
                if (hirer.getPerson() != null) {
                    Hibernate.initialize(hirer.getPerson());
                    if (hirer.getPerson().getLogin() != null) {
                        Hibernate.initialize(hirer.getPerson().getLogin());
                    }
                }
            }
        }

        // Handle Housekeeper and its nested properties
        if (report.getHousekeeper() != null) {
            if (report.getHousekeeper() instanceof Housekeeper) {
                Housekeeper housekeeper = (Housekeeper) report.getHousekeeper(); // Explicitly cast
                Hibernate.initialize(housekeeper.getHires());
                if (housekeeper.getHires() != null) {
                    for (Hire hire : housekeeper.getHires()) {
                        Hibernate.initialize(hire.getReview());
                        Hibernate.initialize(hire.getHirer());
                        if (hire.getHirer() != null && hire.getHirer().getPerson() != null) {
                            Hibernate.initialize(hire.getHirer().getPerson());
                            if (hire.getHirer().getPerson().getLogin() != null) {
                                Hibernate.initialize(hire.getHirer().getPerson().getLogin());
                            }
                        }
                    }
                }
                Hibernate.initialize(housekeeper.getHousekeeperSkills()); // <<< ตรงนี้สำคัญ: โหลด HousekeeperSkills
                if (housekeeper.getHousekeeperSkills() != null) {
                    for (HousekeeperSkill skill : housekeeper.getHousekeeperSkills()) {
                        Hibernate.initialize(skill.getSkillType());
                    }
                }
                if (housekeeper.getPerson() != null) {
                    Hibernate.initialize(housekeeper.getPerson());
                    if (housekeeper.getPerson().getLogin() != null) {
                        Hibernate.initialize(housekeeper.getPerson().getLogin());
                    }
                }
            }
        }

        // Handle Reporter and its person/login
        if (report.getReporter() != null && report.getReporter().getPerson() != null) {
            Hibernate.initialize(report.getReporter().getPerson());
            if (report.getReporter().getPerson().getLogin() != null) {
                Hibernate.initialize(report.getReporter().getPerson().getLogin());
            }
        }

        return report;
    }

    @Override
    @Transactional
    public Report createReport(Report report) {
        // Validate and set Reporter
        if (report.getReporter() != null && report.getReporter().getId() != null) {
            PartyRole existingReporter = partyRoleRepository.findById(report.getReporter().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Reporter not found with ID: " + report.getReporter().getId()));
            report.setReporter(existingReporter);
        } else {
            throw new IllegalArgumentException("Reporter is required and must have a valid ID.");
        }

        // Validate and set Hirer
        if (report.getHirer() != null && report.getHirer().getId() != null) {
            Hirer existingHirer = hirerRepository.findById(report.getHirer().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Hirer not found with ID: " + report.getHirer().getId()));
            report.setHirer(existingHirer);
        } else {
            throw new IllegalArgumentException("Hirer is required and must have a valid ID.");
        }

        // Validate and set Housekeeper (optional)
        if (report.getHousekeeper() != null && report.getHousekeeper().getId() != null) {
            Housekeeper existingHousekeeper = housekeeperRepository.findById(report.getHousekeeper().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Housekeeper not found with ID: " + report.getHousekeeper().getId()));
            report.setHousekeeper(existingHousekeeper);
            // <<< บรรทัดนี้ที่เพิ่มเข้ามาเพื่อโหลด HousekeeperSkills ล่วงหน้า
            // เพื่อป้องกัน LazyInitializationException เมื่อ Serialize response
            Hibernate.initialize(existingHousekeeper.getHousekeeperSkills());
            if (existingHousekeeper.getHousekeeperSkills() != null) {
                for (HousekeeperSkill skill : existingHousekeeper.getHousekeeperSkills()) {
                    Hibernate.initialize(skill.getSkillType());
                }
            }
        } else {
            report.setHousekeeper(null); // Set to null if not provided
        }

        // Validate and set Penalty (optional)
        if (report.getPenalty() != null && report.getPenalty().getPenaltyId() != null) {
            Penalty existingPenalty = penaltyRepository.findById(report.getPenalty().getPenaltyId())
                    .orElseThrow(() -> new IllegalArgumentException("Penalty not found with ID: " + report.getPenalty().getPenaltyId()));
            report.setPenalty(existingPenalty);
        } else {
            report.setPenalty(null); // Set to null if not provided
        }

        // Set default report status if not provided
        if (report.getReportStatus() == null || report.getReportStatus().isEmpty()) {
            report.setReportStatus("pending");
        }

        return reportRepository.save(report);
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
            // <<< บรรทัดนี้ที่เพิ่มเข้ามาใน updateReport ด้วย
            Hibernate.initialize(newHousekeeper.getHousekeeperSkills());
            if (newHousekeeper.getHousekeeperSkills() != null) {
                for (HousekeeperSkill skill : newHousekeeper.getHousekeeperSkills()) {
                    Hibernate.initialize(skill.getSkillType());
                }
            }
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

        return reportRepository.save(existingReport);
    }

    @Override
    @Transactional
    public void deleteReport(int id) {
        reportRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true) // เพิ่ม readOnly = true
    public List<Report> getReportsByStatus(String reportStatus) {
        // อาจจะต้อง Initialize เพิ่มเติมตรงนี้ด้วย หากมีการดึงข้อมูล HousekeeperSkills มาใช้งาน
        List<Report> reports = reportRepository.findByReportStatus(reportStatus);
        for (Report report : reports) {
            if (report.getHousekeeper() instanceof Housekeeper) {
                Hibernate.initialize(((Housekeeper) report.getHousekeeper()).getHousekeeperSkills());
            }
        }
        return reports;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> findByPenaltyId(Integer penaltyId) {
        if (penaltyId == null) {
            return Optional.empty();
        }
        Optional<Report> reportOptional = reportRepository.findByPenalty_PenaltyId(penaltyId);

        if (reportOptional.isPresent()) {
            Report foundReport = reportOptional.get();
            // Initialize associated entities to prevent LazyInitializationException later
            Hibernate.initialize(foundReport.getReporter());
            Hibernate.initialize(foundReport.getHirer());
            Hibernate.initialize(foundReport.getHousekeeper());
            // Penalty is already fetched by the query

            // Initialize nested Person and Login objects
            if (foundReport.getReporter() != null && foundReport.getReporter().getPerson() != null) {
                Hibernate.initialize(foundReport.getReporter().getPerson());
                if (foundReport.getReporter().getPerson().getLogin() != null) {
                    Hibernate.initialize(foundReport.getReporter().getPerson().getLogin());
                }
            }
            if (foundReport.getHirer() != null && foundReport.getHirer() instanceof Hirer) {
                Hirer hirer = (Hirer) foundReport.getHirer(); // Explicitly cast
                if (hirer.getPerson() != null) {
                    Hibernate.initialize(hirer.getPerson());
                    if (hirer.getPerson().getLogin() != null) {
                        Hibernate.initialize(hirer.getPerson().getLogin());
                    }
                }
                // Initialize hires for Hirer if needed for serialization
                Hibernate.initialize(hirer.getHires());
                if (hirer.getHires() != null) {
                    for (Hire hire : hirer.getHires()) {
                        Hibernate.initialize(hire.getReview());
                        Hibernate.initialize(hire.getHousekeeper());
                        if (hire.getHousekeeper() != null && hire.getHousekeeper().getPerson() != null) {
                            Hibernate.initialize(hire.getHousekeeper().getPerson());
                            if (hire.getHousekeeper().getPerson().getLogin() != null) {
                                Hibernate.initialize(hire.getHousekeeper().getPerson().getLogin());
                            }
                        }
                    }
                }
            }
            if (foundReport.getHousekeeper() != null && foundReport.getHousekeeper() instanceof Housekeeper) {
                Housekeeper housekeeper = (Housekeeper) foundReport.getHousekeeper(); // Explicitly cast
                if (housekeeper.getPerson() != null) {
                    Hibernate.initialize(housekeeper.getPerson());
                    if (housekeeper.getPerson().getLogin() != null) {
                        Hibernate.initialize(housekeeper.getPerson().getLogin());
                    }
                }
                // Initialize housekeeperSkills and hires for Housekeeper
                Hibernate.initialize(housekeeper.getHousekeeperSkills());
                if (housekeeper.getHousekeeperSkills() != null) {
                    for (HousekeeperSkill skill : housekeeper.getHousekeeperSkills()) {
                        Hibernate.initialize(skill.getSkillType());
                    }
                }
                Hibernate.initialize(housekeeper.getHires());
                if (housekeeper.getHires() != null) {
                    for (Hire hire : housekeeper.getHires()) {
                        Hibernate.initialize(hire.getReview());
                        Hibernate.initialize(hire.getHirer());
                        if (hire.getHirer() != null && hire.getHirer().getPerson() != null) {
                            Hibernate.initialize(hire.getHirer().getPerson());
                            if (hire.getHirer().getPerson().getLogin() != null) {
                                Hibernate.initialize(hire.getHirer().getPerson().getLogin());
                            }
                        }
                    }
                }
            }
        }
        return reportOptional;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> findLatestReportWithPenaltyByPersonId(Integer personId) {
        List<Report> reports = reportRepository.findReportsWithPenaltyByPersonId(personId);
        if (!reports.isEmpty()) {
            Report latestReport = reports.get(0); // Assuming the query already orders them
            Hibernate.initialize(latestReport.getPenalty());
            Hibernate.initialize(latestReport.getReporter()); // Initialize reporter for completeness
            Hibernate.initialize(latestReport.getHirer()); // Initialize hirer
            Hibernate.initialize(latestReport.getHousekeeper()); // Initialize housekeeper

            // Initialize nested Person and Login objects for Hirer/Housekeeper/Reporter
            if (latestReport.getHirer() != null && latestReport.getHirer() instanceof Hirer) {
                Hirer hirer = (Hirer) latestReport.getHirer(); // Explicitly cast
                if (hirer.getPerson() != null) {
                    Hibernate.initialize(hirer.getPerson());
                    if (hirer.getPerson().getLogin() != null) {
                        Hibernate.initialize(hirer.getPerson().getLogin());
                    }
                }
                Hibernate.initialize(hirer.getHires()); // Initialize hires
            }
            if (latestReport.getHousekeeper() != null && latestReport.getHousekeeper() instanceof Housekeeper) {
                Housekeeper housekeeper = (Housekeeper) latestReport.getHousekeeper(); // Explicitly cast
                if (housekeeper.getPerson() != null) {
                    Hibernate.initialize(housekeeper.getPerson());
                    if (housekeeper.getPerson().getLogin() != null) {
                        Hibernate.initialize(housekeeper.getPerson().getLogin());
                    }
                }
                Hibernate.initialize(housekeeper.getHousekeeperSkills()); // Initialize skills
                if (housekeeper.getHousekeeperSkills() != null) {
                    for (HousekeeperSkill skill : housekeeper.getHousekeeperSkills()) {
                        Hibernate.initialize(skill.getSkillType());
                    }
                }
                Hibernate.initialize(housekeeper.getHires()); // Initialize hires
            }
            if (latestReport.getReporter() != null) {
                if (latestReport.getReporter().getPerson() != null) {
                    Hibernate.initialize(latestReport.getReporter().getPerson());
                    if (latestReport.getReporter().getPerson().getLogin() != null) {
                        Hibernate.initialize(latestReport.getReporter().getPerson().getLogin());
                    }
                }
            }
            return Optional.of(latestReport);
        }
        return Optional.empty();
    }
}