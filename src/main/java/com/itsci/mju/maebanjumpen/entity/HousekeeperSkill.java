package com.itsci.mju.maebanjumpen.entity;

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

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "skill_level_tier_id", nullable = false)
        @ToString.Exclude
        private SkillLevelTier skillLevelTier;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "housekeeper_id", nullable = false)
        @JsonBackReference("housekeeper-skills")
        @ToString.Exclude
        private Housekeeper housekeeper;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "skill_type_id", nullable = false)
        @ToString.Exclude
        private SkillType skillType;

        @Column(name = "price_per_day")
        private Double pricePerDay;

        @Column(name = "total_hires_completed", nullable = false)
        private Integer totalHiresCompleted = 0;
}