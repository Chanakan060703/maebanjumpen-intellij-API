package com.itsci.mju.maebanjumpen.penalty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyDTO {
	private Integer penaltyId;
	private String penaltyType;
	private String penaltyDetail;
	private LocalDateTime penaltyDate;
	private String penaltyStatus;

	// ⬅️ เพิ่ม Report ID เพื่อใช้ในการเชื่อมโยง/อัปเดตตรรกะใน Service
	private Integer reportId;
}