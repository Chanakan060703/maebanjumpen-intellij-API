package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*; // ⬅️ เพิ่ม import ทั้งหมด
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="penalty")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Penalty {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer penaltyId;
	@Column(nullable = false)
	private String penaltyType;
	@Column(nullable = false)
	private String penaltyDetail;
	@Column(nullable = false)
	private LocalDateTime penaltyDate;
	@Column(nullable = false)
	private String penaltyStatus;

	// ----------------- RELATIONSHIP (Inverse Side) -----------------

	// ⬅️ เพิ่มความสัมพันธ์กลับไปที่ Report
	// ใช้ mappedBy เพราะฝั่ง Report เป็นเจ้าของ JoinColumn (penalty_id)
	@OneToOne(mappedBy = "penalty", fetch = FetchType.LAZY)
	private Report report;
}