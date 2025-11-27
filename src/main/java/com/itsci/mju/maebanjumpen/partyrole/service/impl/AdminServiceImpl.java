package com.itsci.mju.maebanjumpen.partyrole.service.impl;

import com.itsci.mju.maebanjumpen.partyrole.dto.AdminDTO;
import com.itsci.mju.maebanjumpen.mapper.AdminMapper;
import com.itsci.mju.maebanjumpen.entity.Admin;
import com.itsci.mju.maebanjumpen.partyrole.repository.AdminRepository;
import com.itsci.mju.maebanjumpen.partyrole.service.AdminService;
import lombok.RequiredArgsConstructor; // ⬅️ แนะนำให้ใช้แทน @Autowired
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException; // ⬅️ ใช้สำหรับจัดการกรณีไม่พบข้อมูล

@Service
@RequiredArgsConstructor
public  class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final AdminRepository adminRepository;


    @Override
    public List<AdminDTO> getAllAdmins() {
        List<Admin> entities = adminRepository.findAll();

        return adminMapper.toDtoList(entities);
    }

    @Override
    public AdminDTO getAdminById(int id) {
        Admin entity = adminRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Admin not found with ID: " + id));

        return adminMapper.toDto(entity);
    }

    @Override
    public AdminDTO saveAdmin(AdminDTO adminDto) {
        Admin entityToSave = adminMapper.toEntity(adminDto);
        Admin savedEntity = adminRepository.save(entityToSave);

        return adminMapper.toDto(savedEntity);
    }

    @Override
    public void deleteAdmin(int id) {
        if (!adminRepository.existsById(id)) {
            throw new NoSuchElementException("Admin not found with ID: " + id);
        }

        adminRepository.deleteById(id);
    }

    @Override
    public AdminDTO updateAdmin(int id, AdminDTO adminDto) {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Admin not found with ID: " + id));

        existingAdmin.setAdminStatus(adminDto.getAdminStatus());
        Admin updatedEntity = adminRepository.save(existingAdmin);

        return adminMapper.toDto(updatedEntity);
    }
}