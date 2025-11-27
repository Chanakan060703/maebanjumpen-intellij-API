package com.itsci.mju.maebanjumpen.entity

import jakarta.persistence.*

@Entity
@Table(name = "skill_level_tier")
data class SkillLevelTier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "skill_level_name")
    var skillLevelName: String? = null,
    @Column(name = "min_hires_for_level")
    var minHiresForLevel: Int? = null
)

