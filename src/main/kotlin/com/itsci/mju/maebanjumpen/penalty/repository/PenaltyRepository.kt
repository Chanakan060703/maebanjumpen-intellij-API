package com.itsci.mju.maebanjumpen.penalty.repository

import com.itsci.mju.maebanjumpen.entity.Penalty
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PenaltyRepository : JpaRepository<Penalty, Int>

