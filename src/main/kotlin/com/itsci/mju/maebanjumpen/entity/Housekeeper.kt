package com.itsci.mju.maebanjumpen.entity

import jakarta.persistence.*

@Entity
@DiscriminatorValue("housekeeper")
open class Housekeeper(
    @Column(name = "photo_verify_url")
    open var photoVerifyUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status_verify")
    open var statusVerify: VerifyStatus? = VerifyStatus.PENDING,

    @Column(name = "rating")
    open var rating: Double? = 0.0,

    @Column(name = "daily_rate")
    open var dailyRate: String? = "0.0",

    @OneToMany(mappedBy = "housekeeper", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var hires: MutableSet<Hire> = mutableSetOf(),

    @OneToMany(mappedBy = "housekeeper", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var housekeeperSkills: MutableSet<HousekeeperSkill> = mutableSetOf()
) : Member() {

    enum class VerifyStatus {
        PENDING, APPROVED, REJECTED, VERIFIED, NOT_VERIFIED
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Housekeeper) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int = super.hashCode()
}

