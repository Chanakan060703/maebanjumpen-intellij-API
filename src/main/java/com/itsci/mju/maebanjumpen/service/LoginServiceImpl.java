package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.LoginDTO;
import com.itsci.mju.maebanjumpen.dto.PartyRoleDTO;
import com.itsci.mju.maebanjumpen.mapper.LoginMapper;
import com.itsci.mju.maebanjumpen.mapper.PartyRoleMapper; // ⬅️ IMPORT MAHPPER
import com.itsci.mju.maebanjumpen.model.*;
import com.itsci.mju.maebanjumpen.model.PasswordUtil;
import com.itsci.mju.maebanjumpen.repository.LoginRepository;
import com.itsci.mju.maebanjumpen.repository.PartyRoleRepository;
import com.itsci.mju.maebanjumpen.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final LoginRepository loginRepository;
    private final PersonRepository personRepository;
    private final PartyRoleRepository partyRoleRepository; // ⬅️ ต้อง Inject PartyRoleRepository
    private final LoginMapper loginMapper;
    private final PartyRoleMapper partyRoleMapper; // ⬅️ INJECT MAPPER

    @Override
    @Transactional(readOnly = true)
    // 🚨 เปลี่ยนให้คืนค่า PartyRoleDTO
    public PartyRoleDTO authenticate(String username, String password) {
        return findPartyRoleByLogin(username, password);
    }

    @Override
    @Transactional
    public LoginDTO saveLogin(LoginDTO loginDto) {
        Login login = loginMapper.toEntity(loginDto);
        if (login.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        login.setPassword(PasswordUtil.hashPassword(login.getPassword()));
        Login savedLogin = loginRepository.save(login);
        return loginMapper.toDto(savedLogin);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginDTO getLoginByUsername(String username) {
        Optional<Login> loginOpt = loginRepository.findById(username);
        return loginOpt.map(loginMapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public void deleteLogin(String username) {
        loginRepository.deleteById(username);
    }

    @Override
    public LoginDTO updateLogin(String username, LoginDTO loginDto) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    // 🚨 เปลี่ยนให้คืนค่า PartyRoleDTO
    public PartyRoleDTO findPartyRoleByLogin(String username, String rawPassword) {
        // 1. ค้นหา Login
        Optional<Login> loginOpt = loginRepository.findByUsername(username);
        if (loginOpt.isEmpty()) {
            return null;
        }

        Login storedLogin = loginOpt.get();
        String storedHash = storedLogin.getPassword();

        // 2. ตรวจสอบรหัสผ่าน
        if (!PasswordUtil.verifyPassword(rawPassword, storedHash)) {
            System.out.println("-> [LoginService] Authentication failed: Invalid password for user: " + username);
            return null;
        }

        // 3. ค้นหา Person (✅ โค้ดที่แก้ไขเพื่อใช้ Optional)
        // 💡 สมมติว่า PersonRepository.findByLoginUsername ได้ถูกแก้ไขให้คืนค่าเป็น Optional<Person> แล้ว
        Optional<Person> personOpt = personRepository.findByLoginUsername(username);
        if (personOpt.isEmpty()) { // ตรวจสอบว่ามี Person หรือไม่
            System.out.println("-> [LoginService] Authentication failed: Person not found for user: " + username);
            return null;
        }
        Person person = personOpt.get(); // ดึง Person object ออกมา

        // 4. ตรวจสอบสถานะบัญชี
        if (!"active".equalsIgnoreCase(person.getAccountStatus())) {
            System.out.println("-> [LoginService] Authentication failed: Account status is inactive for user: " + username);
            return null;
        }

        // 5. ค้นหา PartyRole ด้วยเมธอด @EntityGraph
        // 🚨 ใช้เมธอดใหม่ที่โหลด transactions มาพร้อมกัน
        List<PartyRole> roles = partyRoleRepository.findByPersonPersonId(person.getPersonId());

        if (roles.isEmpty()) {
            return null;
        }

        PartyRole role = roles.get(0);

        // 6. ตรวจสอบสถานะบัญชีสำหรับ Member อีกครั้ง
        if (role instanceof Member && !"active".equalsIgnoreCase(person.getAccountStatus())) {
            return null;
        }

        // 7. 🚨 แปลง Entity ที่โหลดครบถ้วนแล้วเป็น DTO ก่อนคืนค่า
        return partyRoleMapper.toDto(role);
    }
}