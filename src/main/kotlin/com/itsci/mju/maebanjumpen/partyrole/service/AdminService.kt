package com.itsci.mju.maebanjumpen.partyrole.service

import com.itsci.mju.maebanjumpen.partyrole.dto.AdminDTO

interface AdminService {
    fun listAllAdmins(): List<AdminDTO>
    fun getAdminById(id: Int): AdminDTO
    fun createAdmin(adminDto: AdminDTO): AdminDTO
    fun deleteAdmin(id: Int)
    fun updateAdmin(id: Int, adminDto: AdminDTO): AdminDTO
}

