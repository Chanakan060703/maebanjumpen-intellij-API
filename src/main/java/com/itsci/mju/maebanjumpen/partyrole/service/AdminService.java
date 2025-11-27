package com.itsci.mju.maebanjumpen.partyrole.service;

import com.itsci.mju.maebanjumpen.partyrole.dto.AdminDTO;

import java.util.List;

public interface AdminService {
    List<AdminDTO> getAllAdmins();
    AdminDTO getAdminById(int id);

    AdminDTO saveAdmin(AdminDTO adminDto);

    void deleteAdmin(int id);

    AdminDTO updateAdmin(int id, AdminDTO adminDto);
}