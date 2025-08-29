package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException;
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException;
import com.itsci.mju.maebanjumpen.model.Hire;
import java.util.List;

public interface HireService {
    List<Hire> getAllHires();
    Hire getHireById(Integer id);
    List<Hire> getHiresByHirerId(Integer hirerId);
    List<Hire> getHiresByHousekeeperId(Integer housekeeperId);
    Hire saveHire(Hire hire);
    Hire updateHire(Integer id, Hire hire) throws InsufficientBalanceException, HirerNotFoundException;
    void deleteHire(Integer id);
    Hire addProgressionImagesToHire(Integer hireId, List<String> imageUrls);
}