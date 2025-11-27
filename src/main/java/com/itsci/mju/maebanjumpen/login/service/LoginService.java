package com.itsci.mju.maebanjumpen.login.service;

import com.itsci.mju.maebanjumpen.login.dto.LoginDTO;
import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO; // ⬅️ IMPORT DTO

public interface LoginService {
    // 🚨 เปลี่ยนให้คืนค่า PartyRoleDTO
    PartyRoleDTO authenticate(String username, String password);

    // บันทึกข้อมูลล็อกอินใหม่: รับ/คืน LoginDTO
    LoginDTO saveLogin(LoginDTO loginDto);

    // ดึงข้อมูลล็อกอินโดยใช้ username: คืนค่า LoginDTO
    LoginDTO getLoginByUsername(String username);

    // ลบข้อมูลล็อกอิน
    void deleteLogin(String username);

    // อัปเดตข้อมูลล็อกอิน: รับ LoginDTO
    LoginDTO updateLogin(String username, LoginDTO loginDto);

    // 🚨 เปลี่ยนให้คืนค่า PartyRoleDTO
    PartyRoleDTO findPartyRoleByLogin(String username, String password);
}