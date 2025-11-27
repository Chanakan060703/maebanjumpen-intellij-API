package com.itsci.mju.maebanjumpen.partyrole.service.impl;

import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO; // ⬅️ DTO
import com.itsci.mju.maebanjumpen.mapper.PartyRoleMapper; // ⬅️ Mapper
import com.itsci.mju.maebanjumpen.entity.*;
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository;
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository;
import com.itsci.mju.maebanjumpen.partyrole.service.PartyRoleService;
import lombok.RequiredArgsConstructor; // ⬅️ ใช้แทน @Autowired
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // ⬅️ ใช้ DI แบบ Constructor
public class PartyRoleServiceImpl implements PartyRoleService {

    private final PartyRoleRepository partyRoleRepository;
    private final PartyRoleMapper partyRoleMapper; // ⬅️ Inject Mapper
    private final PersonRepository personRepository;

    @Override
    @Transactional
    public PartyRoleDTO savePartyRole(PartyRoleDTO partyRoleDto) {

        // 1. ตรวจสอบว่ามี Person ID ที่ใช้เชื่อมโยงมาใน DTO หรือไม่
        if (partyRoleDto.getPerson() == null || partyRoleDto.getPerson().getPersonId() == null) {
            // หากไม่มี ID, โยน Exception ที่เหมาะสม (เช่น Bad Request หรือ Validation)
            throw new IllegalArgumentException("Person ID is required to create a PartyRole.");
        }

        // 2. ดึง Person Entity ที่มีอยู่
        Integer personId = partyRoleDto.getPerson().getPersonId();
        Person existingPerson = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person ID: " + personId + " not found. Failed to link PartyRole.")); // ⚠️ ตรวจสอบ ID

        // 3. แปลง DTO -> Entity
        PartyRole partyRole = partyRoleMapper.toEntity(partyRoleDto);

        // 4. เชื่อมโยง Person Entity เข้ากับ PartyRole Entity
        partyRole.setPerson(existingPerson); // ⬅️ นี่คือบรรทัดสำคัญที่ขาดหายไป

        // 5. บันทึก PartyRole Entity
        PartyRole savedPartyRole = partyRoleRepository.save(partyRole);

        // 6. แปลง Entity -> DTO
        return partyRoleMapper.toDto(savedPartyRole);
    }


    // 2. getPartyRoleById: คืนค่า PartyRoleDTO
    @Override
    @Transactional(readOnly = true)
    public PartyRoleDTO getPartyRoleById(int id) { // ⬅️ เปลี่ยน Output
        Optional<PartyRole> partyRoleOpt = partyRoleRepository.findById(id);

        // ใช้ map() เพื่อแปลง Optional<Entity> เป็น Optional<DTO>
        return partyRoleOpt.map(partyRoleMapper::toDto)
                .orElse(null); // ⬅️ คืนค่า DTO หรือ null

        // ⚠️ หมายเหตุ: การจัดการ Lazy Loading (เช่น partyRole instanceof Hirer)
        // ควรถูกย้ายไปใช้ @EntityGraph ใน Repository หรือจัดการใน Mapper
        // การบังคับโหลดใน Service นี้อาจไม่จำเป็นหากใช้ Mapper และ EAGER/EntityGraph อย่างถูกต้อง
    }

    // 3. getAllPartyRoles: คืนค่า List<PartyRoleDTO>
    @Override
    @Transactional(readOnly = true)
    public List<PartyRoleDTO> getAllPartyRoles() { // ⬅️ เปลี่ยน Output
        List<PartyRole> partyRoles = partyRoleRepository.findAll();

        // ⬅️ ใช้ Mapper แปลง List<Entity> -> List<DTO>
        return partyRoleMapper.toDtoList(partyRoles);
    }

    // 4. updatePartyRole: รับ DTO, ดึง Entity, อัปเดต, บันทึก, แปลงกลับเป็น DTO
    @Override
    @Transactional
    public PartyRoleDTO updatePartyRole(int id, PartyRoleDTO partyRoleDto) { // ⬅️ เปลี่ยน Input/Output
        // 1. ดึง Entity เดิม
        return partyRoleRepository.findById(id).map(existingPartyRole -> {

            // 2. แปลง DTO ขาเข้าเป็น Entity เพื่อดึงค่าที่ต้องการอัปเดต (แต่ไม่บันทึก)
            PartyRole updatedDetails = partyRoleMapper.toEntity(partyRoleDto);

            // 3. ทำการอัปเดต Field ด้วยมือ (หรือใช้ MapStruct @Mapping target)

            // อัปเดต Person details (ถ้ามี)
            if (existingPartyRole.getPerson() != null && updatedDetails.getPerson() != null) {
                // ⚠️ Logic การอัปเดตที่ซับซ้อนควรถูกย้ายไปใน Mapper หรือมี Helper Method
                // ตัวอย่าง: อัปเดตเฉพาะ Person
                // existingPartyRole.getPerson().setFirstName(updatedDetails.getPerson().getFirstName());
                // ... (อื่นๆ) ...
            }

            // ⚠️ การอัปเดตที่ถูกต้องควรใช้ MapStruct @Mapping(target = "id", ignore = true)
            // และ copy fields ทั้งหมดจาก updatedDetails ไปยัง existingPartyRole
            // แต่เนื่องจากตรรกะการอัปเดตซับซ้อน (มี Subtypes/Relations) จึงมักเขียนด้วยมือ

            // 4. บันทึก
            PartyRole savedRole = partyRoleRepository.save(existingPartyRole);

            // 5. แปลง Entity -> DTO
            return partyRoleMapper.toDto(savedRole);

        }).orElseThrow(() -> new RuntimeException("PartyRole not found with ID: " + id));
    }

    // 5. deletePartyRole: ไม่มี DTO I/O
    @Override
    @Transactional
    public void deletePartyRole(int id) {
        partyRoleRepository.deleteById(id);
    }
}