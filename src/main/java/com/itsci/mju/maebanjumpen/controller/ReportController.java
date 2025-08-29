package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.model.Report;
import com.itsci.mju.maebanjumpen.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // สำหรับการจัดการ Transaction
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/maeban/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired // Constructor Injection เป็นวิธีที่แนะนำ
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    @Transactional(readOnly = true) // เพิ่ม readOnly = true สำหรับ GET methods
    public ResponseEntity<List<Report>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true) // เพิ่ม readOnly = true
    public ResponseEntity<Report> getReportById(@PathVariable int id) {
        Report report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @PostMapping
    @Transactional // เพิ่ม @Transactional เพื่อให้ JPA Session ยังคงเปิดอยู่จนกว่าจะ Serialize response
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        Report createdReport = reportService.createReport(report);
        return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Transactional // สำหรับการอัปเดตข้อมูล
    public ResponseEntity<Report> updateReport(@PathVariable int id, @RequestBody Report report) {
        Report updatedReport = reportService.updateReport(id, report);
        return ResponseEntity.ok(updatedReport);
    }

    @DeleteMapping("/{id}")
    @Transactional // สำหรับการลบข้อมูล
    public ResponseEntity<Void> deleteReport(@PathVariable int id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint ใหม่สำหรับดึงรายงานล่าสุดที่มีบทลงโทษสำหรับ personId ที่ระบุ
    @GetMapping("/latest-with-penalty/by-person/{personId}")
    @Transactional(readOnly = true)
    public ResponseEntity<Report> getLatestReportWithPenaltyByPersonId(@PathVariable Integer personId) {
        Optional<Report> optionalReport = reportService.findLatestReportWithPenaltyByPersonId(personId);
        if (optionalReport.isPresent()) {
            return ResponseEntity.ok(optionalReport.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
