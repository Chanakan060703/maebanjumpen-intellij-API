package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import java.util.List;

public interface HousekeeperService {
    List<Housekeeper> getAllHousekeepers();
    Housekeeper getHousekeeperById(int id);
    Housekeeper saveHousekeeper(Housekeeper housekeeper);
    Housekeeper updateHousekeeper(int id, Housekeeper housekeeper);
    void deleteHousekeeper(int id);

    void calculateAndSetAverageRating(int housekeeperId);

    void addBalance(Integer housekeeperId, Double amount) throws HousekeeperNotFoundException;
    void deductBalance(Integer housekeeperId, Double amount) throws HousekeeperNotFoundException;

    List<Housekeeper> getHousekeepersByStatus(String status);

    // *** เพิ่มเมธอดใหม่นี้ ***
    List<Housekeeper> getNotVerifiedOrNullStatusHousekeepers();
}
