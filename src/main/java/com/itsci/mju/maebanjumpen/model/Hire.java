package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode.Exclude;

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
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_type_id")
    @ToString.Exclude
    @Exclude
    private SkillType skillType;

    @OneToOne(mappedBy = "hire", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Exclude
    private Review review;

//	@ElementCollection
//	@CollectionTable(name = "hire_additional_skills", joinColumns = @JoinColumn(name = "hire_id"))
//	@Column(name = "additional_skill_type_id")
//	private List<Integer> additionalSkillTypeIds;
//
//	public void setReview(Review review) {
//		if (this.review != null && this.review.getHire() != null) {
//			this.review.setHire(null);
//		}
//		this.review = review;
//		if (review != null) {
//			review.setHire(this);
//		}
//	}
}
