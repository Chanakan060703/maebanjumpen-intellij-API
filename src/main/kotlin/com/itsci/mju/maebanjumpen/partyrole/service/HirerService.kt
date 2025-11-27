package com.itsci.mju.maebanjumpen.partyrole.service

import com.itsci.mju.maebanjumpen.partyrole.dto.HirerDTO

interface HirerService {
    fun saveHirer(hirerDto: HirerDTO): HirerDTO
    fun getHirerById(id: Int): HirerDTO
    fun getAllHirers(): List<HirerDTO>
    fun updateHirer(id: Int, hirerDto: HirerDTO): HirerDTO
    fun deleteHirer(id: Int)
    fun deductBalance(hirerId: Int, amount: Double)
    fun addBalance(hirerId: Int, amount: Double)
}

