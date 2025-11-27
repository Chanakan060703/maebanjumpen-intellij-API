package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "penalty")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class Penalty(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,

    @Column(nullable = false)
    var penaltyType: String = "",

    @Column(nullable = false)
    var penaltyDetail: String = "",

    @Column(nullable = false)
    var penaltyDate: LocalDateTime? = null,

    @Column(nullable = false)
    var penaltyStatus: String = "",

    @OneToOne(mappedBy = "penalty", fetch = FetchType.LAZY)
    var report: Report? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Penalty) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Penalty(penaltyId=$id, penaltyType='$penaltyType', penaltyStatus='$penaltyStatus')"
}

