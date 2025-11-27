package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.entity.Housekeeper
import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException
import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperDetailDTO
import com.itsci.mju.maebanjumpen.mapper.HousekeeperMapper
import com.itsci.mju.maebanjumpen.mapper.PersonMapper
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.HousekeeperRepository
import com.itsci.mju.maebanjumpen.partyrole.service.HousekeeperService
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HousekeeperServiceImpl(
    private val housekeeperMapper: HousekeeperMapper,
    private val housekeeperRepository: HousekeeperRepository,
    private val personMapper: PersonMapper,
    private val personRepository: PersonRepository
) : HousekeeperService {

    @Value("\${app.public-base-url}")
    private lateinit var publicBaseUrl: String

    private fun buildFullImageUrl(filename: String?, folderName: String): String? {
        if (filename.isNullOrEmpty()) return null
        if (filename.startsWith("http://") || filename.startsWith("https://")) return filename
        return "$publicBaseUrl/maeban/files/download/$folderName/$filename"
    }

    private fun transformHousekeeperUrls(housekeeper: Housekeeper?): Housekeeper? {
        if (housekeeper == null) return null
        housekeeper.photoVerifyUrl = buildFullImageUrl(housekeeper.photoVerifyUrl, "verify_photos")
        housekeeper.person?.let { person ->
            person.pictureUrl = buildFullImageUrl(person.pictureUrl, "profile_pictures")
        }
        return housekeeper
    }

    private fun transformHireHirerUrls(hires: List<HireDTO>?) {
        if (hires == null) return
        for (hireDto in hires) {
            hireDto.hirer?.person?.let { hirerPersonDto ->
                val originalFilename = hirerPersonDto.pictureUrl
                hirerPersonDto.pictureUrl = buildFullImageUrl(originalFilename, "profile_pictures")
            }
        }
    }

    @Transactional(readOnly = true)
    override fun getAllHousekeepers(): List<HousekeeperDTO> {
        val entities = housekeeperRepository.findAllWithPersonLoginAndSkills()
        return entities
            .map { transformHousekeeperUrls(it) }
            .mapNotNull { it?.let { hk -> housekeeperMapper.toDto(hk) } }
    }

    @Transactional(readOnly = true)
    override fun getHousekeeperDetailById(id: Int): HousekeeperDetailDTO? {
        val housekeeperOptional = housekeeperRepository.findByIdWithAllDetails(id)
        if (housekeeperOptional.isEmpty) return null

        val housekeeper = housekeeperOptional.get()
        val transformedHousekeeper = transformHousekeeperUrls(housekeeper)
        val detailDto = housekeeperMapper.toDetailDto(transformedHousekeeper!!)

        detailDto.hires?.let { transformHireHirerUrls(it) }

        val reviews = detailDto.hires
            ?.mapNotNull { it.review }
            ?: emptyList()

        detailDto.reviews = reviews
        detailDto.hires = null

        return detailDto
    }

    @Transactional
    override fun saveHousekeeper(housekeeperDto: HousekeeperDTO): HousekeeperDTO {
        housekeeperDto.person?.login?.username?.let { username ->
            if (personRepository.findByLoginUsername(username).isPresent) {
                throw IllegalStateException("User with username '$username' already exists. Cannot create duplicate Housekeeper.")
            }
        }

        val housekeeper = housekeeperMapper.toEntity(housekeeperDto)

        housekeeper.person?.let { personRepository.save(it) }

        if (housekeeper.statusVerify == null) {
            housekeeper.statusVerify = Housekeeper.VerifyStatus.NOT_VERIFIED
        }

        val savedHousekeeper = housekeeperRepository.save(housekeeper)
        val transformedHousekeeper = transformHousekeeperUrls(savedHousekeeper)

        return housekeeperMapper.toDto(transformedHousekeeper!!)
    }

    @Transactional
    override fun updateHousekeeper(id: Int, housekeeperDto: HousekeeperDTO): HousekeeperDTO {
        val existingHousekeeper = housekeeperRepository.findById(id)
            .orElseThrow { HousekeeperNotFoundException("Housekeeper with ID $id not found.") }

        if (housekeeperDto.person != null && existingHousekeeper.person != null) {
            val existingPerson = existingHousekeeper.person!!
            existingPerson.email = housekeeperDto.person?.email
            existingPerson.firstName = housekeeperDto.person?.firstName
            existingPerson.lastName = housekeeperDto.person?.lastName
            existingPerson.phoneNumber = housekeeperDto.person?.phoneNumber
            existingPerson.address = housekeeperDto.person?.address
            existingPerson.accountStatus = housekeeperDto.person?.accountStatus
            personRepository.save(existingPerson)
        }

        housekeeperDto.statusVerify?.let {
            existingHousekeeper.statusVerify = Housekeeper.VerifyStatus.valueOf(it)
        }
        existingHousekeeper.dailyRate = housekeeperDto.dailyRate

        val updatedHousekeeper = housekeeperRepository.save(existingHousekeeper)
        val transformedHousekeeper = transformHousekeeperUrls(updatedHousekeeper)

        return housekeeperMapper.toDto(transformedHousekeeper!!)
    }

    @Transactional
    override fun deleteHousekeeper(id: Int) {
        housekeeperRepository.deleteById(id)
    }

    @Transactional
    override fun calculateAndSetAverageRating(housekeeperId: Int) {
        val housekeeperOptional = housekeeperRepository.findById(housekeeperId)

        if (housekeeperOptional.isPresent) {
            val housekeeper = housekeeperOptional.get()
            var averageRating = housekeeperRepository.calculateAverageRatingByHousekeeperId(housekeeperId)

            if (averageRating == null) {
                averageRating = 0.0
            }
            housekeeper.rating = averageRating

            housekeeperRepository.save(housekeeper)
            println("Housekeeper ID: ${housekeeper.id} - Average Rating updated to: ${String.format("%.2f", averageRating)}")
        } else {
            System.err.println("Housekeeper with ID $housekeeperId not found for rating calculation.")
        }
    }

    @Transactional
    override fun addBalance(housekeeperId: Int, amount: Double) {
        val housekeeper = housekeeperRepository.findById(housekeeperId)
            .orElseThrow { HousekeeperNotFoundException("Housekeeper with ID $housekeeperId not found.") }

        val currentBalance = housekeeper.balance ?: 0.0
        housekeeper.balance = currentBalance + amount
        housekeeperRepository.save(housekeeper)
        println("Balance added to housekeeper $housekeeperId: $amount. New balance: ${housekeeper.balance}")
    }

    @Transactional
    override fun deductBalance(housekeeperId: Int, amount: Double) {
        val housekeeper = housekeeperRepository.findById(housekeeperId)
            .orElseThrow { HousekeeperNotFoundException("Housekeeper with ID $housekeeperId not found.") }

        val currentBalance = housekeeper.balance ?: 0.0
        if (currentBalance < amount) {
            throw IllegalStateException("Housekeeper balance is insufficient for deduction.")
        }
        housekeeper.balance = currentBalance - amount
        housekeeperRepository.save(housekeeper)
        println("Balance deducted from housekeeper $housekeeperId: $amount. New balance: ${housekeeper.balance}")
    }

    @Transactional(readOnly = true)
    override fun getHousekeepersByStatus(status: String): List<HousekeeperDTO> {
        val entities = housekeeperRepository.findByStatusVerifyWithDetails(status)
        return entities
            .map { transformHousekeeperUrls(it) }
            .mapNotNull { it?.let { hk -> housekeeperMapper.toDto(hk) } }
    }

    @Transactional(readOnly = true)
    override fun getNotVerifiedOrNullStatusHousekeepers(): List<HousekeeperDTO> {
        val entities = housekeeperRepository.findNotVerifiedOrNullStatusHousekeepersWithDetails()
        return entities
            .map { transformHousekeeperUrls(it) }
            .mapNotNull { it?.let { hk -> housekeeperMapper.toDto(hk) } }
    }
}

