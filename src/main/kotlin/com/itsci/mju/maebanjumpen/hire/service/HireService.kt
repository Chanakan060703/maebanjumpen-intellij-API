package com.itsci.mju.maebanjumpen.hire.service

import com.itsci.mju.maebanjumpen.hire.dto.HireDTO

interface HireService {
    fun getAllHires(): List<HireDTO>
    fun getHireById(id: Int): HireDTO
    fun getHiresByHirerId(hirerId: Int): List<HireDTO>
    fun getHiresByHousekeeperId(housekeeperId: Int): List<HireDTO>
    fun saveHire(hireDto: HireDTO): HireDTO
    fun updateHire(id: Int, hireDto: HireDTO): HireDTO
    fun deleteHire(id: Int)
    fun getCompletedHiresByHousekeeperId(housekeeperId: Int): List<HireDTO>
    fun addProgressionImagesToHire(hireId: Int, imageUrls: List<String>): HireDTO
}

