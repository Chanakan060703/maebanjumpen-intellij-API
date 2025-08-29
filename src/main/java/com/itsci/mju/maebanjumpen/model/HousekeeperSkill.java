package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "housekeeper_skill")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class HousekeeperSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer skillId;

    @Column(name="skillLevel", nullable = false)
    private String skillLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housekeeper_id", nullable = false)
    @JsonBackReference("housekeeper-skills") // Correctly uses JsonBackReference
    @ToString.Exclude
    private Housekeeper housekeeper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_type_id", nullable = false)
    @ToString.Exclude
    private SkillType skillType; // Assuming SkillType is a valid Entity
}