package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.HireDTO;
import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException;
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException;
// import com.itsci.mju.maebanjumpen.model.Hire; // ไม่จำเป็นต้อง import Entity ใน Interface
import java.util.List;

public interface HireService {
    List<HireDTO> getAllHires();
    HireDTO getHireById(Integer id);
    List<HireDTO> getHiresByHirerId(Integer hirerId);
    List<HireDTO> getHiresByHousekeeperId(Integer housekeeperId);

    // ⬅️ แก้ไขชื่อ DTO ให้ถูกต้อง และใช้ DTO ในการรับข้อมูล
    HireDTO saveHire(HireDTO hireDto);

    HireDTO updateHire(Integer id, HireDTO hireDto) throws InsufficientBalanceException, HirerNotFoundException;
    void deleteHire(Integer id);
    List<HireDTO> getCompletedHiresByHousekeeperId(Integer housekeeperId);
    // ⬅️ คืนค่า DTO
    HireDTO addProgressionImagesToHire(Integer hireId, List<String> imageUrls);


}