package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.model.*;
import com.itsci.mju.maebanjumpen.service.PenaltyService;
import com.itsci.mju.maebanjumpen.service.ReportService; // นำเข้า ReportService
import com.itsci.mju.maebanjumpen.service.PersonService; // นำเข้า PersonService
import com.itsci.mju.maebanjumpen.repository.HirerRepository; // นำเข้า HirerRepository
import com.itsci.mju.maebanjumpen.repository.HousekeeperRepository; // นำเข้า HousekeeperRepository

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // เพิ่ม Transactional

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/maeban/penalties")
public class PenaltyController {

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private ReportService reportService; // สำหรับดึง Report มาเพื่อหา Hirer/Housekeeper

    @Autowired
    private PersonService personService; // สำหรับอัปเดต Person

    @Autowired
    private HirerRepository hirerRepository; // สำหรับดึง Hirer
    @Autowired
    private HousekeeperRepository housekeeperRepository; // สำหรับดึง Housekeeper


    @GetMapping
    public ResponseEntity<List<Penalty>> getAllPenalties() {
        List<Penalty> penalties = penaltyService.getAllPenalties();
        return ResponseEntity.ok(penalties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Penalty> getPenaltyById(@PathVariable int id) {
        Penalty penalty = penaltyService.getPenaltyById(id);
        if (penalty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(penalty);
    }

    @PostMapping
    @Transactional // ทำให้ method นี้เป็น Transactional
    public ResponseEntity<Penalty> createPenalty(@RequestBody Penalty penalty,
                                                 @RequestParam(required = false) Integer reportId,
                                                 @RequestParam(required = false) Integer hirerId,
                                                 @RequestParam(required = false) Integer housekeeperId) {
        try {
            // 1. บันทึก Penalty ก่อน เพื่อให้ได้ penaltyId
            Penalty savedPenalty = penaltyService.savePenalty(penalty);

            // 2. อัปเดต accountStatus ของ Person ที่เกี่ยวข้อง
            Person personToUpdate = null;
            if (hirerId != null) {
                Optional<Hirer> hirerOptional = hirerRepository.findById(hirerId);
                if (hirerOptional.isPresent() && hirerOptional.get().getPerson() != null) {
                    personToUpdate = hirerOptional.get().getPerson();
                }
            } else if (housekeeperId != null) {
                Optional<Housekeeper> housekeeperOptional = housekeeperRepository.findById(housekeeperId);
                if (housekeeperOptional.isPresent() && housekeeperOptional.get().getPerson() != null) {
                    personToUpdate = housekeeperOptional.get().getPerson();
                }
            }

            if (personToUpdate != null) {
                personToUpdate.setAccountStatus(savedPenalty.getPenaltyType()); // ตั้งค่าสถานะตาม PenaltyType
                personService.savePerson(personToUpdate); // บันทึกการเปลี่ยนแปลงใน Person
                System.out.println("Updated person account status to: " + savedPenalty.getPenaltyType() + " for person ID: " + personToUpdate.getPersonId());
            } else {
                System.out.println("Warning: Could not find person to update accountStatus for reportId: " + reportId);
            }

            // 3. (Optional) ถ้า Report ถูกอัปเดตใน Controller อื่นๆ อยู่แล้ว ส่วนนี้อาจไม่จำเป็น
            //    แต่ถ้า PenaltyController นี้เป็นจุดเริ่มต้นของกระบวนการทั้งหมด
            //    คุณอาจจะต้องการจัดการการอัปเดต Report ที่นี่ด้วย
            if (reportId != null) {
                Optional<Report> optionalReport = Optional.ofNullable(reportService.getReportById(reportId));
                if (optionalReport.isPresent()) {
                    Report report = optionalReport.get();
                    report.setPenalty(savedPenalty); // เชื่อม Penalty เข้ากับ Report
                    report.setReportStatus("resolved"); // ตั้งสถานะรายงานเป็น 'resolved'
                    reportService.updateReport(reportId, report); // บันทึกการเปลี่ยนแปลงใน Report
                    System.out.println("Updated Report ID: " + reportId + " with penalty ID: " + savedPenalty.getPenaltyId() + " and status 'resolved'");
                } else {
                    System.out.println("Warning: Report with ID " + reportId + " not found for penalty linking.");
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedPenalty); // ส่งสถานะ 201 Created
        } catch (Exception e) {
            System.err.println("Error creating penalty and updating account status: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{id}")
    @Transactional // ทำให้ method นี้เป็น Transactional
    public ResponseEntity<Penalty> updatePenalty(@PathVariable int id, @RequestBody Penalty penalty) {
        try {
            Penalty updatedPenalty = penaltyService.updatePenalty(id, penalty);

            // เมื่อมีการอัปเดต Penalty และอาจมีการเปลี่ยนประเภท Penalty
            // เราควรตรวจสอบว่า Penalty นี้ถูกใช้กับ Report ไหน และใครถูกลงโทษ
            Optional<Report> optionalReport = reportService.findByPenaltyId(updatedPenalty.getPenaltyId()); // ต้องเพิ่ม method นี้ใน ReportService/Repository
            if (optionalReport.isPresent()) {
                Report report = optionalReport.get();
                Person personToUpdate = null;
                if (report.getHirer() != null && report.getHirer().getPerson() != null) {
                    personToUpdate = report.getHirer().getPerson();
                } else if (report.getHousekeeper() != null && report.getHousekeeper().getPerson() != null) {
                    personToUpdate = report.getHousekeeper().getPerson();
                }

                if (personToUpdate != null) {
                    personToUpdate.setAccountStatus(updatedPenalty.getPenaltyType()); // อัปเดตสถานะตาม PenaltyType ใหม่
                    personService.savePerson(personToUpdate); // บันทึกการเปลี่ยนแปลงใน Person
                    System.out.println("Updated person account status to: " + updatedPenalty.getPenaltyType() + " for person ID: " + personToUpdate.getPersonId() + " due to penalty update.");
                }
            } else {
                System.out.println("Warning: Report linked to penalty ID " + updatedPenalty.getPenaltyId() + " not found during penalty update.");
            }

            return ResponseEntity.ok(updatedPenalty);
        } catch (RuntimeException e) {
            System.err.println("Error updating penalty: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // หรือ HttpStatus.BAD_REQUEST
        } catch (Exception e) {
            System.err.println("Error during penalty update: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePenalty(@PathVariable int id) {
        penaltyService.deletePenalty(id);
        return ResponseEntity.noContent().build();
    }
}