package com.itsci.mju.maebanjumpen.hire.service.impl

import com.itsci.mju.maebanjumpen.entity.Hire
import com.itsci.mju.maebanjumpen.entity.Hirer
import com.itsci.mju.maebanjumpen.entity.Housekeeper
import com.itsci.mju.maebanjumpen.entity.SkillType
import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException
import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException
import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.hire.repository.HireRepository
import com.itsci.mju.maebanjumpen.hire.service.HireService
import com.itsci.mju.maebanjumpen.housekeeperskill.service.HousekeeperSkillService
import com.itsci.mju.maebanjumpen.mapper.HireMapper
import com.itsci.mju.maebanjumpen.partyrole.repository.HirerRepository
import com.itsci.mju.maebanjumpen.partyrole.repository.HousekeeperRepository
import com.itsci.mju.maebanjumpen.partyrole.service.HirerService
import com.itsci.mju.maebanjumpen.partyrole.service.HousekeeperService
import com.itsci.mju.maebanjumpen.skilltype.repository.SkillTypeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HireServiceImpl(
    private val hireMapper: HireMapper,
    private val hireRepository: HireRepository,
    private val hirerService: HirerService,
    private val housekeeperService: HousekeeperService,
    private val housekeeperSkillService: HousekeeperSkillService,
    private val skillTypeRepository: SkillTypeRepository,
    private val hirerRepository: HirerRepository,
    private val housekeeperRepository: HousekeeperRepository,
    private val statusUpdateService: HireStatusUpdateService
) : HireService {

    @Transactional(readOnly = true)
    override fun getAllHires(): List<HireDTO> {
        val entities = hireRepository.findAllWithDetails()
        return hireMapper.toDtoList(entities)
    }

    @Transactional(readOnly = true)
    override fun getHireById(id: Int): HireDTO {
        val hire = hireRepository.fetchByIdWithAllDetails(id)
            .orElseThrow { IllegalArgumentException("Hire with ID $id not found.") }
        return hireMapper.toDto(hire)
    }

    @Transactional(readOnly = true)
    override fun getHiresByHirerId(hirerId: Int): List<HireDTO> {
        val hires = hireRepository.findByHirerIdWithDetails(hirerId)
        return hireMapper.toDtoList(hires)
    }

    @Transactional(readOnly = true)
    override fun getHiresByHousekeeperId(housekeeperId: Int): List<HireDTO> {
        val hires = hireRepository.findByHousekeeperIdWithDetails(housekeeperId)
        return hireMapper.toDtoList(hires)
    }

    @Transactional(readOnly = true)
    override fun getCompletedHiresByHousekeeperId(housekeeperId: Int): List<HireDTO> {
        val completedStatus = "Completed"
        val completedHires = hireRepository.findByHousekeeperIdAndJobStatusWithDetails(housekeeperId, completedStatus)
        return hireMapper.toDtoList(completedHires)
    }

    @Transactional
    override fun saveHire(hireDto: HireDTO): HireDTO {
        val hire = hireMapper.toEntity(hireDto)

        val hirer = validateAndGetHirer(hire)
        hire.hirer = hirer

        val housekeeper = validateAndGetHousekeeper(hire)
        hire.housekeeper = housekeeper

        val skillType = validateAndGetSkillType(hire)
        hire.skillType = skillType

        if (hireDto.paymentAmount == null || hireDto.paymentAmount!! <= 0) {
            throw IllegalArgumentException("Payment amount is required and must be a positive value.")
        }
        hire.paymentAmount = hireDto.paymentAmount!!

        if ((hirer.balance ?: 0.0) < hire.paymentAmount) {
            throw InsufficientBalanceException("Insufficient balance to create a hire.")
        }

        hire.hireName = skillType.skillTypeName ?: ""
        hire.hireDetail = hireDto.hireDetail ?: ""
        val savedHire = hireRepository.save(hire)
        val finalHire = hireRepository.fetchByIdWithAllDetails(savedHire.hireId!!).orElse(savedHire)
        return hireMapper.toDto(finalHire)
    }

    @Transactional
    override fun updateHire(id: Int, hireDto: HireDTO): HireDTO {
        val existingHire = hireRepository.fetchByIdWithAllDetails(id)
            .orElseThrow { IllegalArgumentException("Hire with ID $id not found.") }

        val oldStatus = existingHire.jobStatus
        val newStatus = hireDto.jobStatus

        if (newStatus != null && newStatus.equals("Completed", ignoreCase = true)
            && !oldStatus.equals("Completed", ignoreCase = true)) {

            if (existingHire.paymentAmount <= 0) {
                throw IllegalStateException("Cannot complete hire. Payment amount is missing or invalid.")
            }

            hirerService.deductBalance(existingHire.hirer!!.id!!, existingHire.paymentAmount)
            housekeeperService.addBalance(existingHire.housekeeper!!.id!!, existingHire.paymentAmount)

            housekeeperSkillService.updateSkillLevelAndHiresCompleted(
                existingHire.housekeeper!!.id!!,
                existingHire.skillType!!.skillTypeId!!
            )
        }

        newStatus?.let { existingHire.jobStatus = it }
        hireDto.hireName?.let { existingHire.hireName = it }
        hireDto.hireDetail?.let { existingHire.hireDetail = it }
        hireDto.hireDate?.let { existingHire.hireDate = it }
        hireDto.startDate?.let { existingHire.startDate = it }
        hireDto.startTime?.let { existingHire.startTime = it }
        hireDto.endTime?.let { existingHire.endTime = it }
        hireDto.location?.let { existingHire.location = it }

        if (hireDto.skillType?.skillTypeId != null
            && existingHire.skillType?.skillTypeId != hireDto.skillType?.skillTypeId) {
            val newSkillType = skillTypeRepository.findById(hireDto.skillType!!.skillTypeId!!)
                .orElseThrow { IllegalArgumentException("SkillType with ID ${hireDto.skillType?.skillTypeId} not found.") }
            existingHire.skillType = newSkillType
        }

        val updatedHire = hireRepository.save(existingHire)
        val finalHire = hireRepository.fetchByIdWithAllDetails(updatedHire.hireId!!).orElse(updatedHire)
        return hireMapper.toDto(finalHire)
    }

    @Transactional
    override fun deleteHire(id: Int) {
        hireRepository.deleteById(id)
    }

    @Transactional
    override fun addProgressionImagesToHire(hireId: Int, imageUrls: List<String>): HireDTO {
        val hire = hireRepository.fetchByIdWithAllDetails(hireId)
            .orElseThrow { IllegalArgumentException("Hire with ID $hireId not found.") }
        hire.progressionImageUrls.addAll(imageUrls)

        val updatedHire = hireRepository.save(hire)
        return hireMapper.toDto(updatedHire)
    }

    private fun validateAndGetHirer(hire: Hire): Hirer {
        if (hire.hirer?.id == null) {
            throw IllegalArgumentException("Hirer ID is required for creating a hire.")
        }
        return hirerRepository.findById(hire.hirer!!.id!!)
            .orElseThrow { HirerNotFoundException("Hirer with ID ${hire.hirer?.id} not found.") }
    }

    private fun validateAndGetHousekeeper(hire: Hire): Housekeeper {
        if (hire.housekeeper?.id == null) {
            throw IllegalArgumentException("Housekeeper ID is required for creating a hire.")
        }
        return housekeeperRepository.findById(hire.housekeeper!!.id!!)
            .orElseThrow { HousekeeperNotFoundException("Housekeeper with ID ${hire.housekeeper?.id} not found.") }
    }

    private fun validateAndGetSkillType(hire: Hire): SkillType {
        if (hire.skillType?.skillTypeId == null) {
            throw IllegalArgumentException("SkillType ID is required for creating a hire.")
        }
        return skillTypeRepository.findById(hire.skillType!!.skillTypeId!!)
            .orElseThrow { IllegalArgumentException("SkillType with ID ${hire.skillType?.skillTypeId} not found.") }
    }
}

