package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.PenaltyDTO;
import com.itsci.mju.maebanjumpen.model.Penalty;

import java.util.List;

public interface PenaltyService {
    List<PenaltyDTO> getAllPenalties();
    PenaltyDTO getPenaltyById(int id);
    PenaltyDTO savePenalty(PenaltyDTO penaltyDto);
    void deletePenalty(int id);

    PenaltyDTO updatePenalty(int id, PenaltyDTO penaltyDto);
}