package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.entity.Hire
import com.itsci.mju.maebanjumpen.entity.Hirer
import com.itsci.mju.maebanjumpen.entity.HousekeeperSkill
import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException
import com.itsci.mju.maebanjumpen.mapper.HirerMapper
import com.itsci.mju.maebanjumpen.partyrole.dto.HirerDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.HirerRepository
import com.itsci.mju.maebanjumpen.partyrole.service.HirerService
import org.hibernate.Hibernate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HirerServiceImpl(
    private val hirerMapper: HirerMapper,
    private val hirerRepository: HirerRepository
) : HirerService {

    private fun initializeHirerDetails(hirer: Hirer?) {
        if (hirer == null) return

        hirer.person?.let { person ->
            Hibernate.initialize(person)
            person.login?.let { Hibernate.initialize(it) }
        }

        hirer.transactions?.let { transactions ->
            Hibernate.initialize(transactions)
            println("-> [HirerService] โหลด transactions collection สำหรับ Hirer ID: ${hirer.id} สำเร็จ. จำนวน: ${transactions.size}")
        } ?: println("-> [HirerService] Hirer ID: ${hirer.id} transactions collection เป็น null.")

        hirer.hires?.let { hires ->
            Hibernate.initialize(hires)
            println("-> [HirerService] โหลด hires collection สำหรับ Hirer ID: ${hirer.id} สำเร็จ. จำนวน: ${hires.size}")
            for (hire in hires) {
                hire.review?.let { Hibernate.initialize(it) }
                hire.housekeeper?.let { housekeeper ->
                    Hibernate.initialize(housekeeper)
                    housekeeper.person?.let { person ->
                        Hibernate.initialize(person)
                        person.login?.let { Hibernate.initialize(it) }
                    }
                    housekeeper.housekeeperSkills?.let { skills ->
                        Hibernate.initialize(skills)
                        for (skill in skills) {
                            skill.skillType?.let { Hibernate.initialize(it) }
                        }
                    }
                }
            }
        } ?: println("-> [HirerService] Hirer ID: ${hirer.id} hires collection เป็น null.")
    }

    @Transactional
    override fun saveHirer(hirerDto: HirerDTO): HirerDTO {
        val hirerToSave = hirerMapper.toEntity(hirerDto)
        val savedHirer = hirerRepository.save(hirerToSave)
        initializeHirerDetails(savedHirer)
        return hirerMapper.toDto(savedHirer)
    }

    @Transactional(readOnly = true)
    override fun getHirerById(id: Int): HirerDTO {
        val hirer = hirerRepository.findById(id)
            .orElseThrow { HirerNotFoundException("Hirer not found with ID: $id") }
        initializeHirerDetails(hirer)
        return hirerMapper.toDto(hirer)
    }

    @Transactional(readOnly = true)
    override fun getAllHirers(): List<HirerDTO> {
        val hirers = hirerRepository.findAll()
        for (hirer in hirers) {
            initializeHirerDetails(hirer)
        }
        return hirerMapper.toDtoList(hirers)
    }

    @Transactional
    override fun updateHirer(id: Int, hirerDto: HirerDTO): HirerDTO {
        val existingHirer = hirerRepository.findById(id)
            .orElseThrow { HirerNotFoundException("Hirer not found with ID: $id") }

        existingHirer.balance = hirerDto.balance

        if (existingHirer.person != null && hirerDto.person != null) {
            val existingPerson = existingHirer.person!!
            existingPerson.email = hirerDto.person?.email
            existingPerson.firstName = hirerDto.person?.firstName
            existingPerson.lastName = hirerDto.person?.lastName
            existingPerson.idCardNumber = hirerDto.person?.idCardNumber
            existingPerson.phoneNumber = hirerDto.person?.phoneNumber
            existingPerson.address = hirerDto.person?.address
            existingPerson.pictureUrl = hirerDto.person?.pictureUrl
            existingPerson.accountStatus = hirerDto.person?.accountStatus

            if (existingPerson.login != null && hirerDto.person?.login != null) {
                existingPerson.login?.password = hirerDto.person?.login?.password ?: ""
            }
        }

        val updatedHirer = hirerRepository.save(existingHirer)
        initializeHirerDetails(updatedHirer)
        return hirerMapper.toDto(updatedHirer)
    }

    @Transactional
    override fun deleteHirer(id: Int) {
        if (!hirerRepository.existsById(id)) {
            throw HirerNotFoundException("Hirer with ID: $id not found for deletion.")
        }
        hirerRepository.deleteById(id)
    }

    @Transactional
    override fun deductBalance(hirerId: Int, amount: Double) {
        val hirer = hirerRepository.findById(hirerId)
            .orElseThrow { HirerNotFoundException("Hirer with ID $hirerId not found.") }

        val currentBalance = hirer.balance ?: 0.0
        if (currentBalance < amount) {
            throw InsufficientBalanceException("Insufficient balance for hirer ID: $hirerId. Required: $amount, Available: $currentBalance")
        }
        hirer.balance = currentBalance - amount
        hirerRepository.save(hirer)
    }

    @Transactional
    override fun addBalance(hirerId: Int, amount: Double) {
        val hirer = hirerRepository.findById(hirerId)
            .orElseThrow { HirerNotFoundException("Hirer with ID $hirerId not found.") }

        val currentBalance = hirer.balance ?: 0.0
        hirer.balance = currentBalance + amount
        hirerRepository.save(hirer)
    }
}

