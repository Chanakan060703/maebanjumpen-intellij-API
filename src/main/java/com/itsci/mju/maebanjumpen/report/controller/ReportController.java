package com.itsci.mju.maebanjumpen.report.controller;

import com.itsci.mju.maebanjumpen.report.dto.ReportDTO;
import com.itsci.mju.maebanjumpen.exception.AlreadyReportedException;
import com.itsci.mju.maebanjumpen.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/maeban/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        List<ReportDTO> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable int id) {
        ReportDTO report = reportService.getReportById(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportDTO reportDto) {
        try {
            ReportDTO createdReport = reportService.createReport(reportDto);
            return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
        } catch (AlreadyReportedException e) {
            // กรณีรายงานซ้ำ คืนสถานะ 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            // FIX: ส่งข้อความ Error กลับไปเพื่อช่วยในการ Debug Client
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("X-Error-Message", e.getMessage()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> updateReport(@PathVariable int id, @RequestBody ReportDTO reportDto) {
        try {
            ReportDTO updatedReport = reportService.updateReport(id, reportDto);
            return ResponseEntity.ok(updatedReport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable int id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/latest-with-penalty/by-person/{personId}")
    public ResponseEntity<ReportDTO> getLatestReportWithPenaltyByPersonId(@PathVariable Integer personId) {
        Optional<ReportDTO> optionalReport = reportService.findLatestReportWithPenaltyByPersonId(personId);
        return optionalReport.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint for Account Manager to update a user's account status (e.g., ban/unban)
     * based on a report review.
     * Assumes ReportService contains the logic to update the Person entity status.
     * @param personId The ID of the person whose account status is to be updated.
     * @param isBanned The new status (true for banned, false for active).
     * @return 204 No Content on success, 404 Not Found if personId doesn't exist.
     */
    @PutMapping("/account/status/{personId}")
    public ResponseEntity<Void> updateUserAccountStatus(@PathVariable int personId, @RequestParam boolean isBanned) {
        try {
            // Assuming reportService has or can access the method to update user status
            // Note: You must implement reportService.updateUserAccountStatus(personId, isBanned)
            reportService.updateUserAccountStatus(personId, isBanned);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // e.g., Person not found or other runtime issues
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

//ตรวจรายงานซ้ำ
    @GetMapping("/check-duplicate/hire/{hireId}/reporter/{reporterId}")
    public ResponseEntity<Boolean> checkDuplicateReport(
            @PathVariable Integer hireId,
            @PathVariable Integer reporterId) {

        // ใช้เมธอด findByHireIdAndReporterId ที่คุณ implement ไว้ใน ReportService
        Optional<ReportDTO> optionalReport = reportService.findByHireIdAndReporterId(hireId, reporterId);

        // คืนค่า true หากพบรายงาน (หมายความว่าถูกรายงานแล้ว)
        // คืนค่า false หากไม่พบรายงาน
        return ResponseEntity.ok(optionalReport.isPresent());
    }

}
