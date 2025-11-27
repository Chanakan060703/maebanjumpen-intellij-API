package com.itsci.mju.maebanjumpen.penalty.service

import com.itsci.mju.maebanjumpen.penalty.dto.PenaltyDTO

interface PenaltyService {
    fun getAllPenalties(): List<PenaltyDTO>
    fun getPenaltyById(id: Int): PenaltyDTO?

    @Deprecated("Use savePenalty(PenaltyDTO, Int) instead")
    fun savePenalty(penaltyDto: PenaltyDTO): PenaltyDTO

    fun savePenalty(penaltyDto: PenaltyDTO, targetRoleId: Int): PenaltyDTO
    fun deletePenalty(id: Int)
    fun updatePenalty(id: Int, penaltyDto: PenaltyDTO): PenaltyDTO
}

