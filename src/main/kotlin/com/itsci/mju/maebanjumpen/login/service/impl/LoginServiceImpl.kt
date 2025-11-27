package com.itsci.mju.maebanjumpen.login.service.impl

import com.itsci.mju.maebanjumpen.entity.*
import com.itsci.mju.maebanjumpen.login.dto.LoginDTO
import com.itsci.mju.maebanjumpen.login.repository.LoginRepository
import com.itsci.mju.maebanjumpen.login.service.LoginService
import com.itsci.mju.maebanjumpen.partyrole.dto.*
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import org.springframework.stereotype.Service

import org.springframework.transaction.annotation.Transactional

@Service
class LoginServiceImpl(
    private val loginRepository: LoginRepository,
    private val personRepository: PersonRepository,
    private val partyRoleRepository: PartyRoleRepository,
) : LoginService {

    private fun mapLoginToDto(login: Login): LoginDTO {
        return LoginDTO(
            username = login.username,
            password = null
        )
    }

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

    @Transactional(readOnly = true)
    override fun authenticate(username: String, password: String): PartyRoleDTO? {
        return findPartyRoleByLogin(username, password)
    }

    @Transactional
    override fun CreateLogin(loginDto: LoginDTO): LoginDTO {
        requireNotNull(loginDto.username) { "Username cannot be null" }
        requireNotNull(loginDto.password) { "Password cannot be null" }

        val login = Login(
            username = loginDto.username!!,
            password = PasswordUtil.hashPassword(loginDto.password!!)
        )
        val savedLogin = loginRepository.save(login)
        return mapLoginToDto(savedLogin)
    }

    @Transactional(readOnly = true)
    override fun getLoginByUsername(username: String): LoginDTO? {
        return loginRepository.findById(username)
            .map { mapLoginToDto(it) }
            .orElse(null)
    }

    @Transactional
    override fun deleteLogin(username: String) {
        loginRepository.deleteById(username)
    }

    override fun updateLogin(username: String, loginDto: LoginDTO): LoginDTO? {
        return null
    }

    @Transactional(readOnly = true)
    override fun findPartyRoleByLogin(username: String, rawPassword: String): PartyRoleDTO? {
        val loginOpt = loginRepository.findByUsername(username)
        if (loginOpt.isEmpty) {
            return null
        }

        val storedLogin = loginOpt.get()
        val storedHash = storedLogin.password

        if (!PasswordUtil.verifyPassword(rawPassword, storedHash)) {
            println("-> [LoginService] Authentication failed: Invalid password for user: $username")
            return null
        }

        val personOpt = personRepository.findByLoginUsername(username)
        if (personOpt.isEmpty) {
            println("-> [LoginService] Authentication failed: Person not found for user: $username")
            return null
        }
        val person = personOpt.get()

        val status = person.accountStatus
        if (!status.equals("active", ignoreCase = true)) {
            println("-> [LoginService] Authentication failed: Account status is inactive ($status) for user: $username")
            throw BadRequestExceptßion("Account is restricted: $status")
        }

        val roles = partyRoleRepository.findByPersonId(person.id!!)

        if (roles.isEmpty()) {
            return null
        }

        val role = roles[0]
        return mapPartyRoleToDto(role)
    }
}

