package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.HirerDTO;
import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException;
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException;
import com.itsci.mju.maebanjumpen.mapper.HirerMapper;
import com.itsci.mju.maebanjumpen.model.Hirer;
import com.itsci.mju.maebanjumpen.model.Person;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.model.HousekeeperSkill;
import com.itsci.mju.maebanjumpen.repository.HirerRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class HirerServiceImpl implements HirerService {

    @Autowired
    private HirerMapper hirerMapper;
    @Autowired
    private HirerRepository hirerRepository;

    // ... [ส่วนของ initializeHirerDetails คงเดิม] ...
    private void initializeHirerDetails(Hirer hirer) {
        if (hirer == null) {
            return;
        }

        // --- COMMON FOR ALL MEMBER TYPES (Hirer extends Member) ---
        // Initialize Person and Login
        if (hirer.getPerson() != null) {
            Hibernate.initialize(hirer.getPerson());
            if (hirer.getPerson().getLogin() != null) {
                Hibernate.initialize(hirer.getPerson().getLogin());
            }
        }

        // *** สำคัญ: บังคับโหลด transactions collection ***
        if (hirer.getTransactions() != null) {
            Hibernate.initialize(hirer.getTransactions());
            System.out.println("-> [HirerService] โหลด transactions collection สำหรับ Hirer ID: " + hirer.getId() + " สำเร็จ. จำนวน: " + hirer.getTransactions().size());
        } else {
            System.out.println("-> [HirerService] Hirer ID: " + hirer.getId() + " transactions collection เป็น null.");
        }

        // --- SPECIFIC FOR HIRER ---
        // Initialize hires collection
        if (hirer.getHires() != null) {
            Hibernate.initialize(hirer.getHires());
            System.out.println("-> [HirerService] โหลด hires collection สำหรับ Hirer ID: " + hirer.getId() + " สำเร็จ. จำนวน: " + hirer.getHires().size());
            // โหลดรายละเอียดภายใน hires (Housekeeper, Review, SkillType)
            for (Hire hire : hirer.getHires()) {
                if (hire.getReview() != null) {
                    Hibernate.initialize(hire.getReview());
                }
                if (hire.getHousekeeper() != null) {
                    Hibernate.initialize(hire.getHousekeeper()); // โหลด Housekeeper proxy
                    // บังคับโหลด Person และ Login ของ Housekeeper ใน Hire
                    if (hire.getHousekeeper().getPerson() != null) {
                        Hibernate.initialize(hire.getHousekeeper().getPerson());
                        if (hire.getHousekeeper().getPerson().getLogin() != null) {
                            Hibernate.initialize(hire.getHousekeeper().getPerson().getLogin());
                        }
                    }
                    // บังคับโหลด Skills ของ Housekeeper ใน Hire
                    Set<HousekeeperSkill> hkSkills = hire.getHousekeeper().getHousekeeperSkills();
                    if (hkSkills != null) {
                        Hibernate.initialize(hkSkills);
                        for (HousekeeperSkill skill : hkSkills) {
                            if (skill.getSkillType() != null) {
                                Hibernate.initialize(skill.getSkillType());
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("-> [HirerService] Hirer ID: " + hirer.getId() + " hires collection เป็น null.");
        }
    }
    // ... [สิ้นสุด initializeHirerDetails] ...


    @Override
    @Transactional
    public HirerDTO saveHirer(HirerDTO hirerDto) {
        Hirer hirerToSave = hirerMapper.toEntity(hirerDto);
        Hirer savedHirer = hirerRepository.save(hirerToSave);
        initializeHirerDetails(savedHirer);
        return hirerMapper.toDto(savedHirer);
    }

    @Override
    @Transactional(readOnly = true)
    public HirerDTO getHirerById(int id) {
        // 🎯 hirerRepository.findById(id) ตอนนี้ใช้ JOIN FETCH แล้ว
        Hirer hirer = hirerRepository.findById(id)
                .orElseThrow(() -> new HirerNotFoundException("Hirer not found with ID: " + id));

        initializeHirerDetails(hirer);

        return hirerMapper.toDto(hirer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HirerDTO> getAllHirers() {
        // 🎯 hirerRepository.findAll() ตอนนี้ใช้ JOIN FETCH แล้ว
        List<Hirer> hirers = hirerRepository.findAll();

        for (Hirer hirer : hirers) {
            initializeHirerDetails(hirer);
        }

        return hirerMapper.toDtoList(hirers);
    }

    @Override
    @Transactional
    public HirerDTO updateHirer(int id, HirerDTO hirerDto) {
        Hirer existingHirer = hirerRepository.findById(id)
                .orElseThrow(() -> new HirerNotFoundException("Hirer not found with ID: " + id));

        // อัปเดตข้อมูลจาก DTO ลงใน Entity เดิม
        existingHirer.setBalance(hirerDto.getBalance());

        if (existingHirer.getPerson() != null && hirerDto.getPerson() != null) {
            Person existingPerson = existingHirer.getPerson();

            existingPerson.setEmail(hirerDto.getPerson().getEmail());
            existingPerson.setFirstName(hirerDto.getPerson().getFirstName());
            existingPerson.setLastName(hirerDto.getPerson().getLastName());
            existingPerson.setIdCardNumber(hirerDto.getPerson().getIdCardNumber());
            existingPerson.setPhoneNumber(hirerDto.getPerson().getPhoneNumber());
            existingPerson.setAddress(hirerDto.getPerson().getAddress());
            existingPerson.setPictureUrl(hirerDto.getPerson().getPictureUrl());
            existingPerson.setAccountStatus(hirerDto.getPerson().getAccountStatus());

            if (existingPerson.getLogin() != null && hirerDto.getPerson().getLogin() != null) {
                existingPerson.getLogin().setPassword(hirerDto.getPerson().getLogin().getPassword());
            }
        }

        Hirer updatedHirer = hirerRepository.save(existingHirer);
        initializeHirerDetails(updatedHirer);

        return hirerMapper.toDto(updatedHirer);
    }

    @Override
    @Transactional
    public void deleteHirer(int id) {
        if (!hirerRepository.existsById(id)) {
            throw new HirerNotFoundException("Hirer with ID: " + id + " not found for deletion.");
        }
        hirerRepository.deleteById(id);
    }

    // ⬅️ เมธอดที่หายไปกลับมาแล้ว
    @Override
    @Transactional
    public void deductBalance(Integer hirerId, Double amount) throws InsufficientBalanceException, HirerNotFoundException {
        Optional<Hirer> hirerOptional = hirerRepository.findById(hirerId);
        if (hirerOptional.isEmpty()) {
            throw new HirerNotFoundException("Hirer with ID " + hirerId + " not found.");
        }

        Hirer hirer = hirerOptional.get();
        if (hirer.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance for hirer ID: " + hirerId + ". Required: " + amount + ", Available: " + hirer.getBalance());
        }
        hirer.setBalance(hirer.getBalance() - amount);
        hirerRepository.save(hirer);
    }

    // ⬅️ เมธอดที่หายไปกลับมาแล้ว
    @Override
    @Transactional
    public void addBalance(Integer hirerId, Double amount) throws HirerNotFoundException {
        Optional<Hirer> hirerOptional = hirerRepository.findById(hirerId);
        if (hirerOptional.isEmpty()) {
            throw new HirerNotFoundException("Hirer with ID " + hirerId + " not found.");
        }
        Hirer hirer = hirerOptional.get();
        hirer.setBalance(hirer.getBalance() + amount);
        hirerRepository.save(hirer);
    }
}