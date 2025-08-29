package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException;
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException;
import com.itsci.mju.maebanjumpen.model.Hirer;
import com.itsci.mju.maebanjumpen.model.Person;
import com.itsci.mju.maebanjumpen.model.Login;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.HousekeeperSkill;
import com.itsci.mju.maebanjumpen.model.SkillType;
import com.itsci.mju.maebanjumpen.repository.HirerRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class HirerServiceImpl implements HirerService {

    @Autowired
    private HirerRepository hirerRepository;

    /**
     * Helper method เพื่อบังคับโหลด (initialize) Lazy-loaded fields ของ Hirer
     * รวมถึง Person, Login, Transactions และ Hires พร้อมรายละเอียดภายใน Hires
     * @param hirer Hirer object ที่ต้องการ initialize fields.
     */
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
                // REMOVED: ไม่ต้องตั้งค่า username transient field อีกต่อไป
                // เพราะ username จะถูกดึงผ่าน hirer.getPerson().getLogin().getUsername() โดยตรง
                // หรือผ่านเมธอด getUsername() ใน PartyRole/Member
                // hirer.setUsername(hirer.getPerson().getLogin().getUsername()); // <-- บรรทัดนี้ถูกลบออก
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


    @Override
    @Transactional
    public Hirer saveHirer(Hirer hirer) {
        Hirer savedHirer = hirerRepository.save(hirer);
        initializeHirerDetails(savedHirer);
        return savedHirer;
    }

    @Override
    @Transactional(readOnly = true)
    public Hirer getHirerById(int id) {
        Optional<Hirer> result = hirerRepository.findById(id);
        if (result.isPresent()) {
            Hirer hirer = result.get();
            initializeHirerDetails(hirer);
            return hirer;
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hirer> getAllHirers() {
        List<Hirer> hirers = hirerRepository.findAll();
        for (Hirer hirer : hirers) {
            initializeHirerDetails(hirer);
        }
        return hirers;
    }

    @Override
    @Transactional
    public Hirer updateHirer(int id, Hirer hirer) {
        Optional<Hirer> existingHirerOptional = hirerRepository.findById(id);
        if (existingHirerOptional.isEmpty()) {
            return null;
        }
        Hirer existingHirer = existingHirerOptional.get();

        existingHirer.setBalance(hirer.getBalance());

        if (existingHirer.getPerson() != null && hirer.getPerson() != null) {
            Person existingPerson = existingHirer.getPerson();
            Person detailPerson = hirer.getPerson();

            existingPerson.setEmail(detailPerson.getEmail());
            existingPerson.setFirstName(detailPerson.getFirstName());
            existingPerson.setLastName(detailPerson.getLastName());
            existingPerson.setIdCardNumber(detailPerson.getIdCardNumber());
            existingPerson.setPhoneNumber(detailPerson.getPhoneNumber());
            existingPerson.setAddress(detailPerson.getAddress());
            existingPerson.setPictureUrl(detailPerson.getPictureUrl());
            existingPerson.setAccountStatus(detailPerson.getAccountStatus());

            if (existingPerson.getLogin() != null && detailPerson.getLogin() != null) {
                existingPerson.getLogin().setPassword(detailPerson.getLogin().getPassword());
            } else if (existingPerson.getLogin() == null && detailPerson.getLogin() != null) {
                existingPerson.setLogin(detailPerson.getLogin());
            }
        }

        Hirer updatedHirer = hirerRepository.save(existingHirer);
        initializeHirerDetails(updatedHirer);
        return updatedHirer;
    }


    @Override
    @Transactional
    public void deleteHirer(int id) {
        Optional<Hirer> hirerOptional = hirerRepository.findById(id);
        if (hirerOptional.isPresent()) {
            Hirer hirer = hirerOptional.get();
            hirerRepository.delete(hirer);
        } else {
            throw new RuntimeException("ไม่พบ Hirer ด้วย ID: " + id + " สำหรับการลบ.");
        }
    }

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