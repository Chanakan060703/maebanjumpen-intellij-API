package com.itsci.mju.maebanjumpen.partyrole.controller

import com.itsci.mju.maebanjumpen.partyrole.dto.AdminDTO
import com.itsci.mju.maebanjumpen.partyrole.service.AdminService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/admins")
class AdminController(private val adminService: AdminService) {

    @GetMapping
    fun getAllAdmins(): ResponseEntity<List<AdminDTO>> {
        val admins = adminService.getAllAdmins()
        return ResponseEntity.ok(admins)
    }

    @GetMapping("/{id}")
    fun getAdminById(@PathVariable id: Int): ResponseEntity<AdminDTO> {
        val admin = adminService.getAdminById(id)
        return ResponseEntity.ok(admin)
    }

    @PostMapping
    fun createAdmin(@RequestBody admin: AdminDTO): ResponseEntity<AdminDTO> {
        val savedAdmin = adminService.saveAdmin(admin)
        return ResponseEntity.ok(savedAdmin)
    }

    @PutMapping("/{id}")
    fun updateAdmin(@PathVariable id: Int, @RequestBody admin: AdminDTO): ResponseEntity<AdminDTO> {
        val updatedAdmin = adminService.updateAdmin(id, admin)
        return ResponseEntity.ok(updatedAdmin)
    }

    @DeleteMapping("/{id}")
    fun deleteAdmin(@PathVariable id: Int): ResponseEntity<Void> {
        adminService.deleteAdmin(id)
        return ResponseEntity.noContent().build()
    }
}

