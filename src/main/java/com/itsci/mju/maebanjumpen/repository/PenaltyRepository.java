package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Integer> {
}