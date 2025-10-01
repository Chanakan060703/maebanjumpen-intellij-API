package com.itsci.mju.maebanjumpen.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 💡 เพิ่ม import นี้
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // 🎯 แก้ไข: สั่งให้ Jackson เพิกเฉยต่อ field ที่ไม่รู้จัก (เช่น "report")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HireDTO {
	private Integer hireId;
	private String hireName;
	private String hireDetail;
	private Double paymentAmount;
	private LocalDateTime hireDate;
	private LocalDate startDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private String location;
	private String jobStatus;
	private List<String> progressionImageUrls;

	// Use simple DTOs for relationships
	private HirerDTO hirer;
	private HousekeeperDTO housekeeper;
	private SkillTypeDTO skillType;
	private ReviewDTO review; // Use ReviewDTO for OneToOne

}
