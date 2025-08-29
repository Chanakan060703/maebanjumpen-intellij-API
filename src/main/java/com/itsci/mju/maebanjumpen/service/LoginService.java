package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Login;
import com.itsci.mju.maebanjumpen.model.PartyRole;

public interface LoginService {
    // สำหรับการตรวจสอบการล็อกอินและส่งกลับข้อมูลบทบาท
    PartyRole authenticate(String username, String password);

    // บันทึกข้อมูลล็อกอินใหม่
    Login saveLogin(Login login);

    // ดึงข้อมูลล็อกอินโดยใช้ username
    Login getLoginByUsername(String username);

    // ลบข้อมูลล็อกอิน
    void deleteLogin(String username);

    // อัปเดตข้อมูลล็อกอิน
    Login updateLogin(String username, Login login);

    // หาบทบาทจากข้อมูลล็อกอิน
    PartyRole findPartyRoleByLogin(String username, String password);
}