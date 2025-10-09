package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.PenaltyDTO;

import java.util.List;

public interface PenaltyService {
    List<PenaltyDTO> getAllPenalties();

    PenaltyDTO getPenaltyById(int id);

    /**
     * 🛑 **DEPRECATED:** เมธอดเก่าที่ใช้ไม่ได้แล้ว
     */
    @Deprecated
    PenaltyDTO savePenalty(PenaltyDTO penaltyDto);

    /**
     * ✅ **NEW:** บันทึก Penalty และอัปเดตสถานะบัญชีของผู้ถูกลงโทษ
     * @param penaltyDto ข้อมูลการลงโทษ
     * @param targetRoleId ID ของบทบาท (Role ID ของ Hirer/Housekeeper) ที่ต้องการลงโทษ
     * @return PenaltyDTO ที่ถูกบันทึก
     */
    PenaltyDTO savePenalty(PenaltyDTO penaltyDto, Integer targetRoleId); // ✅ เพิ่มเมธอดที่ใช้ targetRoleId

    void deletePenalty(int id);

    PenaltyDTO updatePenalty(int id, PenaltyDTO penaltyDto);
}
