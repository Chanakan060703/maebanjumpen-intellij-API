package com.itsci.mju.maebanjumpen.report.repository

import com.itsci.mju.maebanjumpen.entity.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ReportRepository : JpaRepository<Report, Long> {

    fun findByReportStatus(reportStatus: String): List<Report>

    fun findByPenalty_PenaltyId(penaltyId: Long): Optional<Report>

    fun findReportsWithPenaltyByPersonId(personId: Long): List<Report>

    fun findByHire_HireId(hireId: Long): List<Report>

    fun findByHire_HireIdAndReporter_Id(hireId: Int, reporterId: Long): Optional<Report>
}

