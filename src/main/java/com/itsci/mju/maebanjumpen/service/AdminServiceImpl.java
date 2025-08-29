package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Admin;
import com.itsci.mju.maebanjumpen.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public  class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin getAdminById(int id) {
        return adminRepository.findById(id).orElse(null);
    }

    @Override
    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public void deleteAdmin(int id) {
        Admin admin = adminRepository.getReferenceById(id);
        adminRepository.delete(admin);
    }

    @Override
    public Admin updateAdmin(int id, Admin admin) {
        Admin existingAdmin = adminRepository.getReferenceById(admin.getId());
        if(existingAdmin == null){
            throw new RuntimeException("Admin not found");
        }
        return adminRepository.save(admin);
    }
}