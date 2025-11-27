package com.itsci.mju.maebanjumpen.housekeeperskill.service.impl

import com.itsci.mju.maebanjumpen.entity.SkillLevelTier
import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperSkillDTO
import com.itsci.mju.maebanjumpen.housekeeperskill.repository.HousekeeperSkillRepository
import com.itsci.mju.maebanjumpen.housekeeperskill.service.HousekeeperSkillService
import com.itsci.mju.maebanjumpen.mapper.HousekeeperMapper
import com.itsci.mju.maebanjumpen.mapper.HousekeeperSkillMapper
import com.itsci.mju.maebanjumpen.mapper.SkillLevelTierMapper
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.HousekeeperRepository
import com.itsci.mju.maebanjumpen.skilltype.repository.SkillLevelTierRepository
import com.itsci.mju.maebanjumpen.skilltype.repository.SkillTypeRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
@Transactional
class HousekeeperSkillServiceImpl(
    private val housekeeperMapper: HousekeeperMapper,
    private val housekeeperSkillMapper: HousekeeperSkillMapper,
    private val skillLevelTierMapper: SkillLevelTierMapper,
    private val housekeeperSkillRepository: HousekeeperSkillRepository,
    private val skillLevelTierRepository: SkillLevelTierRepository,
    private val housekeeperRepository: HousekeeperRepository,
    private val skillTypeRepository: SkillTypeRepository
) : HousekeeperSkillService {

    @Transactional(readOnly = true)
    override fun getAllHousekeeperSkills(): List<HousekeeperDTO> {
        val housekeepers = housekeeperRepository.findAll()
        return housekeeperMapper.toDtoList(housekeepers)
    }

    @Transactional(readOnly = true)
    override fun getHousekeeperSkillById(id: Int): HousekeeperSkillDTO? {
        val entity = housekeeperSkillRepository.findById(id).orElse(null)
        return entity?.let { housekeeperSkillMapper.toDto(it) }
    }

    override fun saveHousekeeperSkill(housekeeperSkillDto: HousekeeperSkillDTO): HousekeeperSkillDTO {
        val housekeeperId = housekeeperSkillDto.housekeeperId
        val skillTypeId = housekeeperSkillDto.skillTypeId

        val optionalExistingHs = housekeeperSkillRepository
            .findByHousekeeperIdAndSkillTypeSkillTypeId(housekeeperId!!, skillTypeId!!)

        if (optionalExistingHs.isPresent) {
            val existingSkill = optionalExistingHs.get()
            housekeeperSkillDto.pricePerDay?.let { existingSkill.pricePerDay = it }
            val updatedSkill = housekeeperSkillRepository.save(existingSkill)
            return housekeeperSkillMapper.toDto(updatedSkill)
        }

        val entity = housekeeperSkillMapper.toEntity(housekeeperSkillDto)

        val housekeeper = housekeeperRepository.findById(housekeeperId)
            .orElseThrow { EntityNotFoundException("Housekeeper not found with ID: $housekeeperId") }
        entity.housekeeper = housekeeper

        val skillType = skillTypeRepository.findById(skillTypeId)
            .orElseThrow { EntityNotFoundException("SkillType not found with ID: $skillTypeId") }
        entity.skillType = skillType

        val skillLevelTier = if (housekeeperSkillDto.skillLevelTierId != null) {
            skillLevelTierRepository.findById(housekeeperSkillDto.skillLevelTierId!!)
                .orElseThrow { EntityNotFoundException("SkillLevelTier not found with ID: ${housekeeperSkillDto.skillLevelTierId}") }
        } else {
            skillLevelTierRepository.findAll()
                .minByOrNull { it.minHiresForLevel ?: 0 }
                ?: throw RuntimeException("No SkillLevelTier found. Cannot set initial level.")
        }

        entity.skillLevelTier = skillLevelTier
        entity.totalHiresCompleted = 0

        val savedEntity = housekeeperSkillRepository.save(entity)
        return housekeeperSkillMapper.toDto(savedEntity)
    }

    override fun deleteHousekeeperSkill(id: Int) {
        housekeeperSkillRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun getSkillsByHousekeeperId(housekeeperId: Int): Optional<HousekeeperSkillDTO> {
        val optionalHousekeeper = housekeeperRepository.findById(housekeeperId)

        if (optionalHousekeeper.isPresent && optionalHousekeeper.get().housekeeperSkills?.isNotEmpty() == true) {
            val firstSkill = optionalHousekeeper.get().housekeeperSkills!!.iterator().next()
            return Optional.of(housekeeperSkillMapper.toDto(firstSkill))
        }

        return Optional.empty()
    }

    override fun updateHousekeeperSkill(id: Int, skillDto: HousekeeperSkillDTO): HousekeeperSkillDTO {
        return housekeeperSkillRepository.findById(id).map { existingSkill ->
            skillDto.pricePerDay?.let { existingSkill.pricePerDay = it }
            skillDto.totalHiresCompleted?.let { existingSkill.totalHiresCompleted = it }

            skillDto.skillLevelTierId?.let { tierId ->
                val newTier = skillLevelTierRepository.findById(tierId)
                    .orElseThrow { EntityNotFoundException("SkillLevelTier not found with ID: $tierId") }
                existingSkill.skillLevelTier = newTier
            }

            val updatedSkill = housekeeperSkillRepository.save(existingSkill)
            housekeeperSkillMapper.toDto(updatedSkill)
        }.orElseThrow { NoSuchElementException("HousekeeperSkill not found with ID: $id") }
    }

    @Transactional(readOnly = true)
    override fun findByHousekeeperIdAndSkillTypeId(housekeeperId: Int, skillTypeId: Int): Optional<HousekeeperSkillDTO> {
        val optionalHs = housekeeperSkillRepository.findByHousekeeperIdAndSkillTypeSkillTypeId(housekeeperId, skillTypeId)
        return optionalHs.map { housekeeperSkillMapper.toDto(it) }
    }

    override fun updateSkillLevelAndHiresCompleted(housekeeperId: Int, skillTypeId: Int) {
        val optionalHs = housekeeperSkillRepository.findByHousekeeperIdAndSkillTypeSkillTypeId(housekeeperId, skillTypeId)

        if (optionalHs.isPresent) {
            val hs = optionalHs.get()
            hs.totalHiresCompleted = (hs.totalHiresCompleted ?: 0) + 1
            recalculateSkillLevel(hs)
            housekeeperSkillRepository.save(hs)
        } else {
            System.err.println("HousekeeperSkill not found for housekeeper $housekeeperId and skill $skillTypeId")
        }
    }

    private fun recalculateSkillLevel(hs: com.itsci.mju.maebanjumpen.entity.HousekeeperSkill) {
        val tiers = skillLevelTierRepository.findAll()
            .sortedByDescending { it.minHiresForLevel ?: 0 }

        val currentHires = hs.totalHiresCompleted ?: 0
        var newTier: SkillLevelTier? = hs.skillLevelTier

        for (tier in tiers) {
            if (currentHires >= (tier.minHiresForLevel ?: 0)) {
                newTier = tier
                break
            }
        }

        if (newTier != null && newTier != hs.skillLevelTier) {
            hs.skillLevelTier = newTier
        }
    }
}

