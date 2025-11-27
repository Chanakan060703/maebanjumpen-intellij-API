package com.itsci.mju.maebanjumpen.partyrole.service

import com.itsci.mju.maebanjumpen.housekeeperskill.dto.HousekeeperDetailDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO

interface HousekeeperService {
    fun getAllHousekeepers(): List<HousekeeperDTO>
    fun getHousekeeperDetailById(id: Int): HousekeeperDetailDTO?
    fun saveHousekeeper(housekeeperDto: HousekeeperDTO): HousekeeperDTO
    fun updateHousekeeper(id: Int, housekeeperDto: HousekeeperDTO): HousekeeperDTO
    fun deleteHousekeeper(id: Int)
    fun calculateAndSetAverageRating(housekeeperId: Int)
    fun addBalance(housekeeperId: Int, amount: Double)
    fun deductBalance(housekeeperId: Int, amount: Double)
    fun getHousekeepersByStatus(status: String): List<HousekeeperDTO>
    fun getNotVerifiedOrNullStatusHousekeepers(): List<HousekeeperDTO>
}

