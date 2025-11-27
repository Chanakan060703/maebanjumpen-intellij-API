package com.itsci.mju.maebanjumpen.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "report")
data class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reportId")
    var id: Long? = 0,

    @Column(name = "reportTitle", nullable = false)
    var reportTitle: String,

    @Column(name = "reportMessage", columnDefinition = "TEXT")
    var reportMessage: String,

    @Column(name = "reportDate", nullable = false)
    var reportDate: LocalDateTime,

    @Column(name = "reportStatus", nullable = false)
    var reportStatus: String,


    @Column(name = "reporter_id")
    var reporterId: Long? = 0,

    @Column(name = "hire_id")
    var hireId: Long? = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", insertable = false, updatable = false)
    var reporter: PartyRole? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penalty_id")
    var penalty: Penalty? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hire_id", insertable = false, updatable = false)
    var hire: Hire? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Report) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Report(reportId=$id, reportTitle='$reportTitle', reportStatus='$reportStatus')"
}

