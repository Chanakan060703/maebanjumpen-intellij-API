package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.AccountManagerDTO;
import com.itsci.mju.maebanjumpen.mapper.AccountManagerMapper; // ⬅️ ต้องมี Mapper
import com.itsci.mju.maebanjumpen.model.AccountManager; // ⬅️ ต้องใช้ Entity
import com.itsci.mju.maebanjumpen.repository.AccountManagerRepository;
import lombok.RequiredArgsConstructor; // ⬅️ ใช้แทน @Autowired
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Inject final fields
public class AccountManagerServiceImpl implements AccountManagerService {

    private final AccountManagerRepository accountManagerRepository;
    private final AccountManagerMapper accountManagerMapper; // ⬅️ Inject Mapper

    @Override
    public List<AccountManagerDTO> getAllAccountManagers() {
        // 1. ดึง List<Entity>
        List<AccountManager> entities = accountManagerRepository.findAll();

        // 2. แปลง List<Entity> เป็น List<DTO> และส่งคืน
        return accountManagerMapper.toDtoList(entities);
    }

    @Override
    public AccountManagerDTO getAccountManagerById(int id) {
        // 1. ดึง Entity
        AccountManager entity = accountManagerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account Manager not found with ID: " + id));

        // 2. แปลง Entity เป็น DTO และส่งคืน
        return accountManagerMapper.toDto(entity);
    }

    @Override
    public AccountManagerDTO saveAccountManager(AccountManagerDTO accountManagerDto) {
        // 1. แปลง DTO เป็น Entity
        AccountManager entity = accountManagerMapper.toEntity(accountManagerDto);

        // 2. บันทึก Entity
        AccountManager savedEntity = accountManagerRepository.save(entity);

        // 3. แปลง Entity ที่ถูกบันทึกกลับเป็น DTO และส่งคืน
        return accountManagerMapper.toDto(savedEntity);
    }

    @Override
    public AccountManagerDTO updateAccountManager(int id, AccountManagerDTO accountManagerDto) {
        // 1. ตรวจสอบและดึง Entity เดิม
        AccountManager existingAccountManager = accountManagerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account Manager not found with ID: " + id));

        // 2. อัปเดตข้อมูล (อาจใช้ MapStruct อัปเดตเฉพาะ field ที่ไม่ใช่ null ก็ได้ แต่ในตัวอย่างนี้ใช้ Setter)
        // ต้องมั่นใจว่า DTO มีข้อมูลครบถ้วนสำหรับการอัปเดต
        existingAccountManager.setManagerID(accountManagerDto.getManagerID());
        // 💡 หมายเหตุ: หากมีการอัปเดต field อื่นๆ ใน PartyRole/Person ต้องทำตรงนี้ด้วย
        // ถ้าใช้ MapStruct: mapper.updateAccountManagerFromDto(accountManagerDto, existingAccountManager);

        // 3. บันทึก Entity ที่อัปเดต
        AccountManager updatedEntity = accountManagerRepository.save(existingAccountManager);

        // 4. แปลง Entity เป็น DTO และส่งคืน
        return accountManagerMapper.toDto(updatedEntity);
    }

    @Override
    public void deleteAccountManager(int id) {
        if (!accountManagerRepository.existsById(id)) {
            throw new NoSuchElementException("Account Manager not found with ID: " + id);
        }
        accountManagerRepository.deleteById(id);
    }
}