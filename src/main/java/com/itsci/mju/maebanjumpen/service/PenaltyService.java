package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Penalty;

import java.util.List;

public interface PenaltyService {
    List<Penalty> getAllPenalties();
    Penalty getPenaltyById(int id);
    Penalty savePenalty(Penalty penalty);
    void deletePenalty(int id);

    Penalty updatePenalty(int id, Penalty penalty);
}