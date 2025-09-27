package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.AdminDTO;
import com.itsci.mju.maebanjumpen.mapper.AdminMapper;
import com.itsci.mju.maebanjumpen.model.Admin;
import com.itsci.mju.maebanjumpen.repository.AdminRepository;
import lombok.RequiredArgsConstructor; // ⬅️ แนะนำให้ใช้แทน @Autowired
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException; // ⬅️ ใช้สำหรับจัดการกรณีไม่พบข้อมูล

@Service
// ใช้ @RequiredArgsConstructor เพื่อ Inject Dependencies ผ่าน Constructor แทน @Autowired (เป็น Best Practice)
@RequiredArgsConstructor
public  class AdminServiceImpl implements AdminService {

    // ⬅️ เปลี่ยนการ Inject เป็น final fields (แล้วใช้ @RequiredArgsConstructor)
    private final AdminMapper adminMapper;
    private final AdminRepository adminRepository;

    // ถ้ายังต้องใช้ @Autowired ให้ลบ @RequiredArgsConstructor และคง @Autowired ไว้

    @Override
    public List<AdminDTO> getAllAdmins() {
        // 1. ดึง List<Entity> ทั้งหมด
        List<Admin> entities = adminRepository.findAll();

        // 2. แปลง List<Entity> เป็น List<DTO> โดยใช้ Mapper
        // ✅ แก้ไข: ส่ง 'entities' ที่มีข้อมูล List<Admin> เข้าไป
        return adminMapper.toDtoList(entities);
    }

    @Override
    public AdminDTO getAdminById(int id) {
        // 1. ดึง Entity จาก Repository
        Admin entity = adminRepository.findById(id)
                // ⬅️ จัดการกรณีไม่พบข้อมูลอย่างเหมาะสม
                .orElseThrow(() -> new NoSuchElementException("Admin not found with ID: " + id));

        // 2. แปลง Entity เป็น DTO และคืนค่า
        return adminMapper.toDto(entity);
    }

    @Override
    public AdminDTO saveAdmin(AdminDTO adminDto) {
        // 1. แปลง DTO เป็น Entity
        Admin entityToSave = adminMapper.toEntity(adminDto);

        // 2. บันทึก Entity
        Admin savedEntity = adminRepository.save(entityToSave);

        // 3. แปลง Entity ที่ถูกบันทึกกลับเป็น DTO และคืนค่า
        return adminMapper.toDto(savedEntity);
    }

    @Override
    public void deleteAdmin(int id) {
        // 1. ตรวจสอบว่ามีข้อมูลอยู่จริงหรือไม่
        if (!adminRepository.existsById(id)) {
            throw new NoSuchElementException("Admin not found with ID: " + id);
        }

        // 2. ลบข้อมูล
        adminRepository.deleteById(id);
    }

    @Override
    public AdminDTO updateAdmin(int id, AdminDTO adminDto) {
        // 1. ดึง Entity เดิมเพื่ออัปเดต
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Admin not found with ID: " + id));

        // 2. อัปเดตข้อมูลจาก DTO ลงใน Entity เดิม
        // ⬅️ เราต้องกำหนดค่า field เอง หรือใช้เมธอดอัปเดตของ Mapper
        existingAdmin.setAdminStatus(adminDto.getAdminStatus());
        // ⚠️ ต้องจัดการอัปเดต field อื่น ๆ ใน Person/PartyRole ด้วย

        // 3. บันทึก Entity ที่อัปเดต
        Admin updatedEntity = adminRepository.save(existingAdmin);

        // 4. แปลง Entity เป็น DTO และคืนค่า
        return adminMapper.toDto(updatedEntity);
    }
}