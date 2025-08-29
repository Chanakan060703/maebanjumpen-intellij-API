package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.*;
import com.itsci.mju.maebanjumpen.repository.PartyRoleRepository;
import com.itsci.mju.maebanjumpen.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- เพิ่ม import นี้

import java.util.List;
import java.util.Optional;

@Service
public class PartyRoleServiceImpl implements PartyRoleService {

    @Autowired
    private PartyRoleRepository partyRoleRepository;

    @Autowired
    private PersonRepository personRepository; // อาจจะไม่ได้ใช้โดยตรงในนี้ แต่เก็บไว้

    @Override
    @Transactional // สำหรับการบันทึก
    public PartyRole savePartyRole(PartyRole partyRole) {
        return partyRoleRepository.save(partyRole);
    }

    @Override
    @Transactional(readOnly = true) // <--- เพิ่ม @Transactional(readOnly = true)
    public PartyRole getPartyRoleById(int id) {
        // หาก PartyRole มีความสัมพันธ์แบบ LAZY ที่คุณต้องการให้โหลด
        // ต้องบังคับโหลดใน Transaction นี้
        PartyRole partyRole = partyRoleRepository.findById(id).orElse(null);
        if (partyRole instanceof Hirer) {
            // ((Hirer) partyRole).getHires().size(); // ถ้าต้องการ hires ของ Hirer
        } else if (partyRole instanceof Housekeeper) {
            // ((Housekeeper) partyRole).getHires().size(); // ถ้าต้องการ hires ของ Housekeeper
            // ((Housekeeper) partyRole).getHousekeeperSkills().size(); // ถ้าต้องการ skills ของ Housekeeper
        }
        return partyRole;
    }

    @Override
    @Transactional(readOnly = true) // <--- เพิ่ม @Transactional(readOnly = true)
    public List<PartyRole> getAllPartyRoles() {
        List<PartyRole> partyRoles = partyRoleRepository.findAll();
        // หากมี PartyRole ที่มีความสัมพันธ์แบบ LAZY ที่คุณต้องการให้โหลด
        // ต้องวนลูปบังคับโหลดแต่ละตัว
        for (PartyRole partyRole : partyRoles) {
            if (partyRole instanceof Hirer) {
                // ((Hirer) partyRole).getHires().size();
            } else if (partyRole instanceof Housekeeper) {
                // ((Housekeeper) partyRole).getHires().size();
                // ((Housekeeper) partyRole).getHousekeeperSkills().size();
            }
        }
        return partyRoles;
    }

    @Override
    @Transactional // สำหรับการอัปเดต
    public PartyRole updatePartyRole(int id, PartyRole partyRole) {
        if (partyRoleRepository.existsById(id)) {
            // ดึง Entity ที่มีอยู่เพื่ออัปเดต ไม่ใช่สร้างใหม่แล้ว set id
            // เพราะอาจมี field อื่นๆ ที่ไม่ได้ส่งมาใน partyRole object ใหม่
            PartyRole existingPartyRole = partyRoleRepository.findById(id).orElse(null);
            if (existingPartyRole != null) {
                // ทำการ copy properties จาก partyRole ไปยัง existingPartyRole
                // หรือใช้ model mapper
                // ตัวอย่างง่ายๆ: existingPartyRole.setSomeField(partyRole.getSomeField());
                return partyRoleRepository.save(existingPartyRole);
            }
        }
        return null;
    }

    @Override
    @Transactional // สำหรับการลบ
    public void deletePartyRole(int id) {
        partyRoleRepository.deleteById(id);
    }
}