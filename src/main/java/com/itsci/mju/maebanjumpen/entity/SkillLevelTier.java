package com.itsci.mju.maebanjumpen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skill_level_tier")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillLevelTier {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(name = "skill_level_name", nullable = false, unique = true)
        private String skillLevelName;

        @Column(name = "min_hires_for_level", nullable = false)
        private Integer minHiresForLevel;

}
