package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Admin;

import java.util.List;

public interface AdminService {
    List<Admin> getAllAdmins();
    Admin getAdminById(int id);
    Admin saveAdmin(Admin admin);
    void deleteAdmin(int id);

    Admin updateAdmin(int id, Admin admin);
}