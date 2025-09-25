package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itsci.mju.maebanjumpen.serializer.HireSerializer;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "hire")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonSerialize(using = HireSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true) // <-- เพิ่มบรรทัดนี้เพื่อละเว้น field ที่ไม่รู้จัก
public class Hire {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer hireId;

	@Column(nullable = false)
	private String hireName;

	@Column(nullable = false)
	private String hireDetail;

	@Column(nullable = false)
	private Double paymentAmount;

	@Column(nullable = false)
	private LocalDateTime hireDate;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalTime startTime;

	@Column(nullable = true)
	private LocalTime endTime;

	@Column(nullable = false)
	private String location;

	@Column(nullable = false)
	private String jobStatus;

	@ElementCollection
	private List<String> progressionImageUrls;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "hirer_id")
	@ToString.Exclude
	@Exclude
	private Hirer hirer;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "housekeeper_id")
	@ToString.Exclude
	@Exclude
	private Housekeeper housekeeper;

	@OneToOne(mappedBy = "hire", fetch = FetchType.LAZY)
	@ToString.Exclude
	@Exclude
	private Review review;

	public void setReview(Review review) {
		if (this.review != null && this.review.getHire() != null) {
			this.review.setHire(null);
		}
		this.review = review;
		if (review != null) {
			review.setHire(this);
		}
	}
}
