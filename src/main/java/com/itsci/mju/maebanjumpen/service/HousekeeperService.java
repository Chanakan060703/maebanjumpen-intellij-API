package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.HousekeeperDTO;
import com.itsci.mju.maebanjumpen.dto.HousekeeperDetailDTO; // 💡 เพิ่ม import นี้
import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import java.util.List;

public interface HousekeeperService {
    List<HousekeeperDTO> getAllHousekeepers();

    // 🎯 การแก้ไข: เปลี่ยนเมธอดเดิมเป็น DetailDTO หรือเพิ่มเมธอดใหม่
    // เลือกที่จะเพิ่มเมธอดใหม่เพื่อให้เมธอด getAllHousekeepers ยังคงใช้ HousekeeperDTO ตัวเล็กได้
    HousekeeperDetailDTO getHousekeeperDetailById(int id); // ⬅️ เปลี่ยนตรงนี้

    // HousekeeperDTO getHousekeeperById(int id); // เมธอดเดิมถูกลบหรือเปลี่ยนชื่อไปใช้ DTO ที่เล็กกว่า

    HousekeeperDTO saveHousekeeper(HousekeeperDTO housekeeperDto);
    HousekeeperDTO updateHousekeeper(int id, HousekeeperDTO housekeeperDto);
    void deleteHousekeeper(int id);

    void calculateAndSetAverageRating(int housekeeperId);

    void addBalance(Integer housekeeperId, Double amount) throws HousekeeperNotFoundException;
    void deductBalance(Integer housekeeperId, Double amount) throws HousekeeperNotFoundException;

    List<HousekeeperDTO> getHousekeepersByStatus(String status);

    List<HousekeeperDTO> getNotVerifiedOrNullStatusHousekeepers();
}