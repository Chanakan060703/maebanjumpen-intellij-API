package com.itsci.mju.maebanjumpen.report.service

import com.itsci.mju.maebanjumpen.report.dto.ReportDTO
import com.itsci.mju.maebanjumpen.report.request.CreateReportRequest
import com.itsci.mju.maebanjumpen.report.request.UpdateReportRequest
import java.util.Optional

interface ReportService {
    fun listAllReports(): List<ReportDTO>

    fun createReport(request: CreateReportRequest): ReportDTO

    fun getReportById(reportId: Long): ReportDTO

    fun deleteReport(id: Long) : Boolean

    fun getReportsByStatus(reportStatus: String): List<ReportDTO>

    fun updateReport(request: UpdateReportRequest): ReportDTO

    fun findByPenaltyId(penaltyId: Long?): Optional<ReportDTO>

    fun findLatestReportWithPenaltyByPersonId(personId: Long?): Optional<ReportDTO>

//    fun findByHireIdAndReporterId(hireId: Long?, reporterId: Long?): Optional<ReportDTO>

    fun updateUserAccountStatus(personId: Long, isBanned: Boolean)
}

