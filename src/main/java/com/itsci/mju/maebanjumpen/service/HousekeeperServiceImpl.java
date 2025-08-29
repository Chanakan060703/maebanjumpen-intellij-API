package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.Person;
import com.itsci.mju.maebanjumpen.repository.HousekeeperRepository;
import com.itsci.mju.maebanjumpen.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // <<< Import นี้
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <<< Import นี้

@Service
public class HousekeeperServiceImpl implements HousekeeperService {

    private final HousekeeperRepository housekeeperRepository;
    private final PersonRepository personRepository;

    @Value("${app.public-base-url}") // <<< เพิ่มตรงนี้!
    private String publicBaseUrl;

    @Autowired
    public HousekeeperServiceImpl(HousekeeperRepository housekeeperRepository, PersonRepository personRepository) {
        this.housekeeperRepository = housekeeperRepository;
        this.personRepository = personRepository;
    }

    // Helper method เพื่อสร้าง URL เต็มจากชื่อไฟล์
    private String buildFullImageUrl(String filename, String folderName) {
        if (filename == null || filename.isEmpty()) {
            return null; // หรือคืนค่า URL ของรูปภาพ default
        }
        // ตรวจสอบว่า filename เป็น URL เต็มอยู่แล้วหรือไม่ (กรณีที่ถูกบันทึกผิดพลาดไปแล้ว หรือเป็น URL จากภายนอก)
        if (filename.startsWith("http://") || filename.startsWith("https://")) {
            return filename;
        }
        return publicBaseUrl + "/maeban/files/download/" + folderName + "/" + filename;
    }

    // Helper method สำหรับแปลง URL ของ Housekeeper และ Person.pictureUrl
    private Housekeeper transformHousekeeperUrls(Housekeeper housekeeper) {
        if (housekeeper == null) {
            return null;
        }
        // แปลง photoVerifyUrl ของ Housekeeper
        housekeeper.setPhotoVerifyUrl(buildFullImageUrl(housekeeper.getPhotoVerifyUrl(), "verify_photos"));

        // แปลง pictureUrl ของ Person (ถ้ามี)
        if (housekeeper.getPerson() != null) {
            housekeeper.getPerson().setPictureUrl(buildFullImageUrl(housekeeper.getPerson().getPictureUrl(), "profile_pictures"));
        }
        return housekeeper;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Housekeeper> getAllHousekeepers() {
        // ใช้ findAllWithPersonLoginAndSkills() เพื่อดึงข้อมูลที่ Eager Fetch ไว้แล้ว
        return housekeeperRepository.findAllWithPersonLoginAndSkills().stream()
                .map(this::transformHousekeeperUrls) // <<< แปลง URL ตรงนี้
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Housekeeper getHousekeeperById(int id) {
        // ใช้ findByIdWithPersonLoginAndSkills() เพื่อดึงข้อมูลที่ Eager Fetch ไว้แล้ว
        Optional<Housekeeper> housekeeperOptional = housekeeperRepository.findByIdWithPersonLoginAndSkills(id);
        return housekeeperOptional
                .map(this::transformHousekeeperUrls) // <<< แปลง URL ตรงนี้
                .orElse(null);
    }

    @Override
    @Transactional
    public Housekeeper saveHousekeeper(Housekeeper housekeeper) {
        if (housekeeper.getPerson() != null) {
            personRepository.save(housekeeper.getPerson());
        }
        // กำหนดค่า statusVerify เป็น "not verified" หากยังไม่ได้กำหนด
        if (housekeeper.getStatusVerify() == null || housekeeper.getStatusVerify().isEmpty()) {
            housekeeper.setStatusVerify("not verified");
        }
        // ก่อน save: photoVerifyUrl ควรเป็นแค่ชื่อไฟล์ (เพราะ DB เก็บแค่ชื่อไฟล์)
        // หลัง save: แปลง URL สำหรับ response ที่ส่งกลับไปให้ client
        Housekeeper savedHousekeeper = housekeeperRepository.save(housekeeper);
        return transformHousekeeperUrls(savedHousekeeper); // <<< แปลง URL สำหรับ response
    }

    @Override
    @Transactional
    public Housekeeper updateHousekeeper(int id, Housekeeper housekeeper) {
        Optional<Housekeeper> existingHousekeeperOptional = housekeeperRepository.findById(id);
        if (existingHousekeeperOptional.isPresent()) {
            Housekeeper existingHousekeeper = existingHousekeeperOptional.get();

            if (housekeeper.getPerson() != null && existingHousekeeper.getPerson() != null) {
                Person existingPerson = existingHousekeeper.getPerson();
                existingPerson.setEmail(housekeeper.getPerson().getEmail());
                existingPerson.setFirstName(housekeeper.getPerson().getFirstName());
                existingPerson.setLastName(housekeeper.getPerson().getLastName());
                existingPerson.setPhoneNumber(housekeeper.getPerson().getPhoneNumber());
                existingPerson.setAddress(housekeeper.getPerson().getAddress());
                // อย่าอัปเดต pictureUrl ตรงๆ จาก request body หากมันเป็น URL เต็ม
                // หากมีการอัปโหลดรูปโปรไฟล์ ควรมี endpoint แยกต่างหาก
                // existingPerson.setPictureUrl(housekeeper.getPerson().getPictureUrl());
                existingPerson.setAccountStatus(housekeeper.getPerson().getAccountStatus());
                personRepository.save(existingPerson);
            }

            // PhotoVerifyUrl ก็ควรถูกอัปเดตผ่าน FileUploadController เท่านั้น
            // ไม่ควรอัปเดตจาก request body ของ Housekeeper update โดยตรง
            // existingHousekeeper.setPhotoVerifyUrl(housekeeper.getPhotoVerifyUrl());
            existingHousekeeper.setStatusVerify(housekeeper.getStatusVerify());
            existingHousekeeper.setDailyRate(housekeeper.getDailyRate());

            Housekeeper updatedHousekeeper = housekeeperRepository.save(existingHousekeeper);
            return transformHousekeeperUrls(updatedHousekeeper); // <<< แปลง URL สำหรับ response
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteHousekeeper(int id) {
        housekeeperRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void calculateAndSetAverageRating(int housekeeperId) {
        Optional<Housekeeper> housekeeperOptional = housekeeperRepository.findById(housekeeperId);

        if (housekeeperOptional.isPresent()) {
            Housekeeper housekeeper = housekeeperOptional.get();
            Double averageRating = housekeeperRepository.calculateAverageRatingByHousekeeperId(housekeeperId);

            if (averageRating == null) {
                averageRating = 0.0;
            }
            housekeeper.setRating(averageRating);

            housekeeperRepository.save(housekeeper);
            System.out.println("Housekeeper ID: " + housekeeper.getId() + " - Average Rating updated to: " + String.format("%.2f", averageRating));
        } else {
            System.err.println("Housekeeper with ID " + housekeeperId + " not found for rating calculation.");
        }
    }

    @Override
    @Transactional
    public void addBalance(Integer housekeeperId, Double amount) throws HousekeeperNotFoundException {
        if (housekeeperId == null) {
            throw new IllegalArgumentException("Housekeeper ID cannot be null for adding balance.");
        }
        Optional<Housekeeper> housekeeperOptional = housekeeperRepository.findById(housekeeperId);
        if (housekeeperOptional.isEmpty()) {
            throw new HousekeeperNotFoundException("Housekeeper with ID " + housekeeperId + " not found.");
        }

        Housekeeper housekeeper = housekeeperOptional.get();
        double currentBalance = housekeeper.getBalance() != null ? housekeeper.getBalance() : 0.0;
        housekeeper.setBalance(currentBalance + amount);
        housekeeperRepository.save(housekeeper);
        System.out.println("Balance added to housekeeper " + housekeeperId + ": " + amount + ". New balance: " + housekeeper.getBalance());
    }

    @Override
    @Transactional
    public void deductBalance(Integer housekeeperId, Double amount) throws HousekeeperNotFoundException {
        if (housekeeperId == null) {
            throw new IllegalArgumentException("Housekeeper ID cannot be null for deducting balance.");
        }
        Optional<Housekeeper> housekeeperOptional = housekeeperRepository.findById(housekeeperId);
        if (housekeeperOptional.isEmpty()) {
            throw new HousekeeperNotFoundException("Housekeeper with ID " + housekeeperId + " not found.");
        }

        Housekeeper housekeeper = housekeeperOptional.get();
        double currentBalance = housekeeper.getBalance() != null ? housekeeper.getBalance() : 0.0;
        if (currentBalance < amount) {
            throw new IllegalStateException("Housekeeper balance is insufficient for deduction.");
        }
        housekeeper.setBalance(currentBalance - amount);
        housekeeperRepository.save(housekeeper);
        System.out.println("Balance deducted from housekeeper " + housekeeperId + ": " + amount + ". New balance: " + housekeeper.getBalance());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Housekeeper> getHousekeepersByStatus(String status) {
        // ใช้ findByStatusVerifyWithDetails() เพื่อดึงข้อมูลที่ Eager Fetch ไว้แล้ว
        return housekeeperRepository.findByStatusVerifyWithDetails(status).stream()
                .map(this::transformHousekeeperUrls) // <<< แปลง URL ตรงนี้
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Housekeeper> getNotVerifiedOrNullStatusHousekeepers() {
        // ใช้ findNotVerifiedOrNullStatusHousekeepersWithDetails() เพื่อดึงข้อมูลที่ Eager Fetch ไว้แล้ว
        return housekeeperRepository.findNotVerifiedOrNullStatusHousekeepersWithDetails().stream()
                .map(this::transformHousekeeperUrls) // <<< แปลง URL ตรงนี้
                .collect(Collectors.toList());
    }
}