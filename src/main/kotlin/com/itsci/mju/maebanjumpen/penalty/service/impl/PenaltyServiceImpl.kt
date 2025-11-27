package com.itsci.mju.maebanjumpen.penalty.service.impl

import com.itsci.mju.maebanjumpen.mapper.PenaltyMapper
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository
import com.itsci.mju.maebanjumpen.penalty.dto.PenaltyDTO
import com.itsci.mju.maebanjumpen.penalty.repository.PenaltyRepository
import com.itsci.mju.maebanjumpen.penalty.service.PenaltyService
import com.itsci.mju.maebanjumpen.person.service.PersonService
import com.itsci.mju.maebanjumpen.report.repository.ReportRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PenaltyServiceImpl(
    private val penaltyMapper: PenaltyMapper,
    private val penaltyRepository: PenaltyRepository,
    private val reportRepository: ReportRepository,
    private val personService: PersonService,
    private val partyRoleRepository: PartyRoleRepository
) : PenaltyService {

    override fun getAllPenalties(): List<PenaltyDTO> {
        val penalties = penaltyRepository.findAll()
        return penaltyMapper.toDtoList(penalties)
    }

    override fun getPenaltyById(id: Int): PenaltyDTO? {
        return penaltyRepository.findById(id)
            .map { penaltyMapper.toDto(it) }
            .orElse(null)
    }

    @Deprecated("Use savePenalty(PenaltyDTO, Int) instead")
    @Transactional
    override fun savePenalty(penaltyDto: PenaltyDTO): PenaltyDTO {
        throw UnsupportedOperationException("Method savePenalty(PenaltyDTO) is deprecated. Use savePenalty(PenaltyDTO, Int) instead.")
    }

    @Transactional
    override fun savePenalty(penaltyDto: PenaltyDTO, targetRoleId: Int): PenaltyDTO {
        val penalty = penaltyMapper.toEntity(penaltyDto)
        val savedPenalty = penaltyRepository.save(penalty)

        val reportId = penaltyDto.reportId
        if (reportId != null) {
            val optionalReport = reportRepository.findById(reportId)

            if (optionalReport.isPresent) {
                val report = optionalReport.get()
                report.penalty = savedPenalty
                report.reportStatus = "RESOLVED"
                reportRepository.save(report)

                updateAccountStatus(targetRoleId, savedPenalty.penaltyType ?: "")
            } else {
                System.err.println("Warning: Report ID $reportId not found for new Penalty. Linking skipped.")
            }
        } else {
            System.err.println("Error: reportId is missing from PenaltyDTO. Cannot link Penalty to Report or update account status.")
        }

        return penaltyMapper.toDto(savedPenalty)
    }

    @Transactional
    private fun updateAccountStatus(targetRoleId: Int, penaltyType: String) {
        val optionalPartyRole = partyRoleRepository.findById(targetRoleId)

        if (optionalPartyRole.isPresent) {
            val partyRole = optionalPartyRole.get()
            val personToUpdate = partyRole.person

            if (personToUpdate != null) {
                personService.updateAccountStatus(personToUpdate.personId!!, penaltyType)
                println("Updated person account status to: $penaltyType for person ID: ${personToUpdate.personId}")
            } else {
                System.err.println("Error: Person object is missing for Role ID: $targetRoleId. Cannot update account status.")
            }
        } else {
            System.err.println("Error: Target PartyRole not found with ID: $targetRoleId. Cannot apply penalty.")
        }
    }

    @Transactional
    override fun deletePenalty(id: Int) {
        val optionalPenalty = penaltyRepository.findById(id)

        if (optionalPenalty.isPresent) {
            val penaltyToDelete = optionalPenalty.get()

            penaltyToDelete.report?.reportId?.let { reportId ->
                reportRepository.findById(reportId).ifPresent { report ->
                    report.penalty = null
                    report.reportStatus = "RESOLVED"
                    reportRepository.save(report)
                    println("Penalty ID $id was unlinked from Report ID ${report.reportId}")
                }
            }
            penaltyRepository.delete(penaltyToDelete)
        }
    }

    @Transactional
    override fun updatePenalty(id: Int, penaltyDto: PenaltyDTO): PenaltyDTO {
        val existingPenalty = penaltyRepository.findById(id)
            .orElseThrow { RuntimeException("Penalty not found with id: $id") }

        val penaltyTypeChanged = penaltyDto.penaltyType != null &&
                existingPenalty.penaltyType != penaltyDto.penaltyType

        penaltyDto.penaltyType?.let { existingPenalty.penaltyType = it }
        penaltyDto.penaltyDetail?.let { existingPenalty.penaltyDetail = it }
        penaltyDto.penaltyDate?.let { existingPenalty.penaltyDate = it }
        penaltyDto.penaltyStatus?.let { existingPenalty.penaltyStatus = it }

        val updatedPenalty = penaltyRepository.save(existingPenalty)

        if (penaltyTypeChanged) {
            System.err.println("Warning: Skipping account status update in updatePenalty method because the target person ID cannot be reliably determined from the existing entities.")
        }

        return penaltyMapper.toDto(updatedPenalty)
    }
}

