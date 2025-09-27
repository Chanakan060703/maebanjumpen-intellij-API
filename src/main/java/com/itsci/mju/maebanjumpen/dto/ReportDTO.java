package com.itsci.mju.maebanjumpen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Integer reportId;
    private String reportTitle;
    private String reportMessage;
    private LocalDateTime reportDate;
    private String reportStatus;

    // 🎯 ใช้ ID สำหรับความสัมพันธ์เมื่อเป็น Request หรือเมื่อไม่ต้องการโหลด Object เต็ม
    private Integer reporterId; // Reporter is a PartyRole
    private Integer hirerId; // Hirer is a Member
    private Integer housekeeperId; // Housekeeper is a Member
    private Integer penaltyId;
    private Integer hireId;
}
