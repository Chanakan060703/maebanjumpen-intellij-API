package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.AdminDTO;

import java.util.List;

public interface AdminService {
    List<AdminDTO> getAllAdmins();
    AdminDTO getAdminById(int id);

    // ⬅️ เปลี่ยนเป็นรับ AdminDTO เข้ามา
    AdminDTO saveAdmin(AdminDTO adminDto);

    void deleteAdmin(int id);

    // ⬅️ เปลี่ยนเป็นรับ AdminDTO เข้ามา และคืนค่า DTO
    AdminDTO updateAdmin(int id, AdminDTO adminDto);
}