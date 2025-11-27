package com.itsci.mju.maebanjumpen.report.service.impl

import com.itsci.mju.maebanjumpen.entity.*
import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.hire.repository.HireRepository
import com.itsci.mju.maebanjumpen.hire.service.impl.HireStatusUpdateService
import com.itsci.mju.maebanjumpen.partyrole.dto.*
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository
import com.itsci.mju.maebanjumpen.penalty.dto.PenaltyDTO
import com.itsci.mju.maebanjumpen.penalty.repository.PenaltyRepository
import com.itsci.mju.maebanjumpen.person.service.PersonService
import com.itsci.mju.maebanjumpen.report.dto.ReportDTO
import com.itsci.mju.maebanjumpen.report.repository.ReportRepository
import com.itsci.mju.maebanjumpen.report.request.CreateReportRequest
import com.itsci.mju.maebanjumpen.report.request.UpdateReportRequest
import com.itsci.mju.maebanjumpen.report.service.ReportService
import com.luca.intern.common.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.Optional

@Service
@Transactional(readOnly = true)
class ReportServiceImpl @Autowired internal constructor(
  private val reportRepository: ReportRepository,
  private val partyRoleRepository: PartyRoleRepository,
  private val penaltyRepository: PenaltyRepository,
  private val hireRepository: HireRepository,
  private val personService: PersonService,
  private val hireStatusUpdateService: HireStatusUpdateService
) : ReportService {

  private fun mapPartyRoleToDto(partyRole: PartyRole): PartyRoleDTO {
    return when (partyRole) {
      is Hirer -> HirerDTO().apply {
        id = partyRole.id?.toInt()
        balance = partyRole.balance
      }
      is Housekeeper -> HousekeeperDTO().apply {
        id = partyRole.id?.toInt()
        balance = partyRole.balance
        photoVerifyUrl = partyRole.photoVerifyUrl
        statusVerify = partyRole.statusVerify?.name
        rating = partyRole.rating
        dailyRate = partyRole.dailyRate
      }
      is Admin -> AdminDTO().apply {
        id = partyRole.id?.toInt()
        adminStatus = partyRole.adminStatus
      }
      is AccountManager -> AccountManagerDTO().apply {
        id = partyRole.id?.toInt()
        managerID = partyRole.managerID?.toInt()
      }
      is Member -> MemberDTO().apply {
        id = partyRole.id?.toInt()
        balance = partyRole.balance
      }
      else -> MemberDTO().apply {
        id = partyRole.id?.toInt()
      }
    }
  }

  private fun mapReportToDto(report: Report): ReportDTO {
    return ReportDTO(
        id = report.id,
        reportTitle = report.reportTitle,
        reportMessage = report.reportMessage,
        reportDate = report.reportDate,
        reportStatus = report.reportStatus,
        reporter = report.reporter?.let { mapPartyRoleToDto(it) },
        penalty = report.penalty?.let {
          PenaltyDTO(
              id = it.id,
              penaltyType = it.penaltyType,
              penaltyDetail = it.penaltyDetail,
              penaltyDate = it.penaltyDate,
              penaltyStatus = it.penaltyStatus
          )
        },
        hire = report.hire?.let { HireDTO(id = it.id, hireName = it.hireName, jobStatus = it.jobStatus) }
    )
  }

  override fun listAllReports(): List<ReportDTO> {
    return reportRepository.findAll().map { mapReportToDto(it) }
  }

  @Transactional
  override fun createReport(request: CreateReportRequest): ReportDTO {
    partyRoleRepository.findById(request.reporterId)
        .orElseThrow { IllegalArgumentException("Reporter not found with id: ${request.reporterId}") }
    hireRepository.findById(request.hireId)
        .orElseThrow { IllegalArgumentException("Hire not found with id: ${request.hireId}") }

    val report = Report(
        reportTitle = request.reportTitle ?: "",
        reportMessage = request.reportMessage ?: "",
        reportDate = LocalDateTime.now(),
        reportStatus = request.reportStatus ?: "",
        reporterId = request.reporterId,
        hireId = request.hireId
    )
    val savedReport = reportRepository.save(report)
    return mapReportToDto(savedReport)
  }

  override fun getReportById(reportId: Long): ReportDTO {
    val report = reportRepository.findById(reportId)
        .orElseThrow { IllegalArgumentException("Report not found with id: $reportId") }
    return mapReportToDto(report)
  }

  @Transactional
  override fun deleteReport(id: Long): Boolean {
    reportRepository.findById(id)
        .orElseThrow { IllegalArgumentException("Report not found with id: $id") }
    reportRepository.deleteById(id)
    return true
  }

  override fun getReportsByStatus(reportStatus: String): List<ReportDTO> {
    return reportRepository.findByReportStatus(reportStatus).map { mapReportToDto(it) }
  }

  @Transactional
  override fun updateReport(request: UpdateReportRequest): ReportDTO {
    val report = reportRepository.findById(request.id)
        .orElseThrow { IllegalArgumentException("Report not found with id: ${request.id}") }

    request.reportTitle?.let { report.reportTitle = it }
    request.reportMessage?.let { report.reportMessage = it }
    request.reportStatus?.let { report.reportStatus = it }
    if (request.reporterId > 0) {
      partyRoleRepository.findById(request.reporterId)
          .orElseThrow { IllegalArgumentException("Reporter not found with id: ${request.reporterId}") }
      report.reporterId = request.reporterId
    }

    val savedReport = reportRepository.save(report)
    return mapReportToDto(savedReport)
  }

  override fun findByPenaltyId(penaltyId: Long?): Optional<ReportDTO> {
    if (penaltyId == null) return Optional.empty()
    return reportRepository.findByPenalty_PenaltyId(penaltyId)
        .map { mapReportToDto(it) }
  }

  override fun findLatestReportWithPenaltyByPersonId(personId: Long?): Optional<ReportDTO> {
    if (personId == null) return Optional.empty()
    val reports = reportRepository.findReportsWithPenaltyByPersonId(personId)
    return if (reports.isNotEmpty()) {
      Optional.of(mapReportToDto(reports.first()))
    } else {
      Optional.empty()
    }
  }

  @Transactional
  override fun updateUserAccountStatus(personId: Long, isBanned: Boolean) {
    val person = personService.getPersonById(personId)
        ?: throw IllegalArgumentException("Person not found with id: $personId")
    person.accountStatus = if (isBanned) "banned" else "active"
    personService.updatePerson(personId, person)
  }


}

