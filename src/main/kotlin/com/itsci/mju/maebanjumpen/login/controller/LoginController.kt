package com.itsci.mju.maebanjumpen.login.controller

import com.itsci.mju.maebanjumpen.exception.AccountStatusException
import com.itsci.mju.maebanjumpen.login.dto.LoginDTO
import com.itsci.mju.maebanjumpen.login.service.LoginService
import com.itsci.mju.maebanjumpen.other.dto.ErrorResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/login")
@CrossOrigin(origins = ["*"])
class LoginController(private val loginService: LoginService) {

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody credentials: Map<String, String>): ResponseEntity<*> {
        val username = credentials["username"]
        val password = credentials["password"]

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Username and password are required"))
        }

        return try {
            val partyRole = loginService.authenticate(username, password)

            if (partyRole == null) {
                val errorDto = ErrorResponseDTO(
                    "INVALID_CREDENTIALS",
                    null,
                    "Invalid username or password."
                )
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto)
            } else {
                ResponseEntity.ok(partyRole)
            }
        } catch (e: AccountStatusException) {
            System.err.println("Authentication blocked due to account status: ${e.accountStatus}")
            val errorDto = ErrorResponseDTO(
                "ACCOUNT_RESTRICTED",
                e.accountStatus,
                "Account is restricted. Status: ${e.accountStatus}"
            )
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto)
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown authentication error"
            val errorDto = ErrorResponseDTO(
                "UNKNOWN_ERROR",
                null,
                "An unexpected error occurred: $errorMessage"
            )
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto)
        }
    }

    @PostMapping
    fun createLogin(@RequestBody loginDto: LoginDTO): ResponseEntity<LoginDTO> {
        val savedLogin = loginService.saveLogin(loginDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLogin)
    }

    @PutMapping("/{username}")
    fun updateLogin(@PathVariable username: String, @RequestBody loginDto: LoginDTO): ResponseEntity<LoginDTO?> {
        if (username != loginDto.username) {
            return ResponseEntity.badRequest().body(null)
        }

        val updatedLogin = loginService.updateLogin(username, loginDto)
        return if (updatedLogin != null) {
            ResponseEntity.ok(updatedLogin)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{username}")
    fun deleteLogin(@PathVariable username: String): ResponseEntity<Void> {
        loginService.deleteLogin(username)
        return ResponseEntity.noContent().build()
    }
}

