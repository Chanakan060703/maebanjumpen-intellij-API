package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.ReportDTO; // ⬅️ ใช้ DTO เท่านั้น
import com.itsci.mju.maebanjumpen.exception.AlreadyReportedException; // นำเข้า Exception ที่อาจเกิดขึ้น
import com.itsci.mju.maebanjumpen.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/maeban/reports")
@RequiredArgsConstructor // ⬅️ ใช้ Constructor Injection
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAllReports() { // ⬅️ ใช้ DTO
        List<ReportDTO> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable int id) { // ⬅️ ใช้ DTO
        ReportDTO report = reportService.getReportById(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportDTO reportDto) { // ⬅️ ใช้ DTO
        try {
            ReportDTO createdReport = reportService.createReport(reportDto);
            return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
        } catch (AlreadyReportedException e) {
            // กรณีรายงานซ้ำ คืนสถานะ 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            // กรณี ID ไม่ถูกต้อง หรือข้อมูลขาด
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> updateReport(@PathVariable int id, @RequestBody ReportDTO reportDto) { // ⬅️ ใช้ DTO
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
    public ResponseEntity<ReportDTO> getLatestReportWithPenaltyByPersonId(@PathVariable Integer personId) { // ⬅️ ใช้ DTO
        Optional<ReportDTO> optionalReport = reportService.findLatestReportWithPenaltyByPersonId(personId);
        return optionalReport.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}