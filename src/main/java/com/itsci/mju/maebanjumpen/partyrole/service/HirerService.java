package com.itsci.mju.maebanjumpen.partyrole.service;

import com.itsci.mju.maebanjumpen.partyrole.dto.HirerDTO;
import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException;
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException;

import java.util.List;

public interface HirerService {
    HirerDTO saveHirer(HirerDTO hirerDto);
    HirerDTO getHirerById(int id);
    List<HirerDTO> getAllHirers();
    HirerDTO updateHirer(int id, HirerDTO hirerDto);
    void deleteHirer(int id);

    void deductBalance(Integer hirerId, Double amount) throws InsufficientBalanceException, HirerNotFoundException;
    void addBalance(Integer hirerId, Double amount) throws HirerNotFoundException;


}