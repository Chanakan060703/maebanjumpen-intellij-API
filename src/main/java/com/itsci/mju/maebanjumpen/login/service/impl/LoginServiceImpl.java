package com.itsci.mju.maebanjumpen.login.service.impl;

import com.itsci.mju.maebanjumpen.login.dto.LoginDTO;
import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO;
import com.itsci.mju.maebanjumpen.exception.AccountStatusException; // ⬅️ เพิ่ม import
import com.itsci.mju.maebanjumpen.mapper.LoginMapper;
import com.itsci.mju.maebanjumpen.mapper.PartyRoleMapper;
import com.itsci.mju.maebanjumpen.entity.*;
import com.itsci.mju.maebanjumpen.login.repository.LoginRepository;
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository;
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository;
import com.itsci.mju.maebanjumpen.login.service.LoginService;
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
    private final PartyRoleRepository partyRoleRepository;
    private final LoginMapper loginMapper;
    private final PartyRoleMapper partyRoleMapper;

    @Override
    @Transactional(readOnly = true)
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
    public PartyRoleDTO findPartyRoleByLogin(String username, String rawPassword) {
        Optional<Login> loginOpt = loginRepository.findByUsername(username);
        if (loginOpt.isEmpty()) {
            return null;
        }

        Login storedLogin = loginOpt.get();
        String storedHash = storedLogin.getPassword();

        if (!PasswordUtil.verifyPassword(rawPassword, storedHash)) {
            System.out.println("-> [LoginService] Authentication failed: Invalid password for user: " + username);
            return null;
        }

        Optional<Person> personOpt = personRepository.findByLoginUsername(username);
        if (personOpt.isEmpty()) {
            System.out.println("-> [LoginService] Authentication failed: Person not found for user: " + username);
            return null;
        }
        Person person = personOpt.get();

        String status = person.getAccountStatus();
        if (!"active".equalsIgnoreCase(status)) {
            System.out.println("-> [LoginService] Authentication failed: Account status is inactive (" + status + ") for user: " + username);
            throw new AccountStatusException("Account is restricted: " + status, status);
        }

        List<PartyRole> roles = partyRoleRepository.findByPersonPersonId(person.getPersonId());

        if (roles.isEmpty()) {
            return null;
        }

        PartyRole role = roles.get(0);

        return partyRoleMapper.toDto(role);
    }
}
