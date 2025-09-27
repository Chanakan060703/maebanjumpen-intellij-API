package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.HousekeeperDTO;
import com.itsci.mju.maebanjumpen.dto.HousekeeperDetailDTO;
import com.itsci.mju.maebanjumpen.dto.ReviewDTO; // ต้องใช้ ReviewDTO
import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException;
// import com.itsci.mju.maebanjumpen.mapper.HousekeeperDetailMapper; // ❌ ลบการ Import นี้
import com.itsci.mju.maebanjumpen.mapper.HousekeeperMapper;
import com.itsci.mju.maebanjumpen.mapper.PersonMapper;
import com.itsci.mju.maebanjumpen.mapper.ReviewMapper; // 💡 เพิ่ม ReviewMapper
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.Person;
import com.itsci.mju.maebanjumpen.repository.HousekeeperRepository;
import com.itsci.mju.maebanjumpen.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HousekeeperServiceImpl implements HousekeeperService {

    // 🎯 Dependencies ที่จำเป็นทั้งหมด
    private final HousekeeperMapper housekeeperMapper;
    // private final HousekeeperDetailMapper housekeeperDetailMapper; // ❌ ลบ Field นี้
    private final HousekeeperRepository housekeeperRepository;
    private final PersonMapper personMapper;
    private final PersonRepository personRepository;
    private final ReviewMapper reviewMapper; // 💡 เพิ่ม ReviewMapper

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    // --- Helper Methods (URL Transformation) ---
    // ... (เมธอด buildFullImageUrl และ transformHousekeeperUrls เหมือนเดิม)
    private String buildFullImageUrl(String filename, String folderName) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        if (filename.startsWith("http://") || filename.startsWith("https://")) {
            return filename;
        }
        return publicBaseUrl + "/maeban/files/download/" + folderName + "/" + filename;
    }

    private Housekeeper transformHousekeeperUrls(Housekeeper housekeeper) {
        if (housekeeper == null) {
            return null;
        }
        housekeeper.setPhotoVerifyUrl(buildFullImageUrl(housekeeper.getPhotoVerifyUrl(), "verify_photos"));
        if (housekeeper.getPerson() != null) {
            housekeeper.getPerson().setPictureUrl(buildFullImageUrl(housekeeper.getPerson().getPictureUrl(), "profile_pictures"));
        }
        return housekeeper;
    }

    // 💡 NEW: ตรรกะการดึง Review จาก Hire List (ย้ายมาจาก Mapper)
    private List<ReviewDTO> extractReviewsFromHires(List<Hire> hires) {
        if (hires == null) return Collections.emptyList();

        return hires.stream()
                .filter(hire -> hire.getReview() != null)
                .map(Hire::getReview)
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    // --- Service Implementation Methods ---

    @Override
    @Transactional(readOnly = true)
    public List<HousekeeperDTO> getAllHousekeepers() {
        // ใช้ Repositoy method ที่โหลดเฉพาะ Person/Login/Skills (เพื่อประสิทธิภาพ)
        List<Housekeeper> entities = housekeeperRepository.findAllWithPersonLoginAndSkills();

        return entities.stream()
                .map(this::transformHousekeeperUrls)
                .map(housekeeperMapper::toDto)
                .collect(Collectors.toList());
    }

    // 🎯 เมธอดสำหรับหน้ารายละเอียดแม่บ้าน (ใช้ตรรกะการแปลงด้วยมือ)
    @Override
    @Transactional(readOnly = true)
    public HousekeeperDetailDTO getHousekeeperDetailById(int id) {
        // ใช้ Query ที่ดึง hires และ review มาแล้ว
        Optional<Housekeeper> housekeeperOptional = housekeeperRepository.findByIdWithAllDetails(id);

        if (housekeeperOptional.isEmpty()) {
            return null;
        }

        Housekeeper housekeeper = housekeeperOptional.get();

        // 1. แปลง URL
        Housekeeper transformedHousekeeper = this.transformHousekeeperUrls(housekeeper);

        // 2. แปลง Entity เป็น DTO (toDetailDto จะทำการคำนวณ jobsCompleted)
        HousekeeperDetailDTO detailDto = housekeeperMapper.toDetailDto(transformedHousekeeper);

        // 3. 💡 โหลด Reviews ที่จำเป็นต้องทำ Manual Mapping
        // ใช้ List<Hire> ที่ดึงมาพร้อมกับ Entity แล้ว
        List<ReviewDTO> reviews = extractReviewsFromHires(housekeeper.getHires().stream().collect(Collectors.toList()));
        detailDto.setReviews(reviews);

        return detailDto;
    }

    // 💡 NEW: ต้องเพิ่มเมธอด toDetailDto ใน HousekeeperMapper (ดูส่วนที่ 3)

    @Override
    @Transactional
    public HousekeeperDTO saveHousekeeper(HousekeeperDTO housekeeperDto) {
        // ... (โค้ด saveHousekeeper เหมือนเดิม)
        if (housekeeperDto.getPerson() != null && housekeeperDto.getPerson().getLogin() != null) {
            String username = housekeeperDto.getPerson().getLogin().getUsername();

            if (personRepository.findByLoginUsername(username).isPresent()) {
                throw new IllegalStateException("User with username '" + username + "' already exists. Cannot create duplicate Housekeeper.");
            }
        }

        Housekeeper housekeeper = housekeeperMapper.toEntity(housekeeperDto);

        if (housekeeper.getPerson() != null) {
            personRepository.save(housekeeper.getPerson());
        }

        if (housekeeper.getStatusVerify() == null || housekeeper.getStatusVerify().toString().isEmpty()) {
            housekeeper.setStatusVerify(Housekeeper.VerifyStatus.NOT_VERIFIED);
        }

        Housekeeper savedHousekeeper = housekeeperRepository.save(housekeeper);
        Housekeeper transformedHousekeeper = transformHousekeeperUrls(savedHousekeeper);

        return housekeeperMapper.toDto(transformedHousekeeper);
    }

    // ... (เมธอด updateHousekeeper, deleteHousekeeper, calculateAndSetAverageRating, addBalance, deductBalance, getHousekeepersByStatus, getNotVerifiedOrNullStatusHousekeepers เหมือนเดิม)

    @Override
    @Transactional
    public HousekeeperDTO updateHousekeeper(int id, HousekeeperDTO housekeeperDto) {
        Housekeeper existingHousekeeper = housekeeperRepository.findById(id)
                .orElseThrow(() -> new HousekeeperNotFoundException("Housekeeper with ID " + id + " not found."));

        // 1. อัปเดตข้อมูล Person
        if (housekeeperDto.getPerson() != null && existingHousekeeper.getPerson() != null) {
            Person existingPerson = existingHousekeeper.getPerson();
            existingPerson.setEmail(housekeeperDto.getPerson().getEmail());
            existingPerson.setFirstName(housekeeperDto.getPerson().getFirstName());
            existingPerson.setLastName(housekeeperDto.getPerson().getLastName());
            existingPerson.setPhoneNumber(housekeeperDto.getPerson().getPhoneNumber());
            existingPerson.setAddress(housekeeperDto.getPerson().getAddress());
            existingPerson.setAccountStatus(housekeeperDto.getPerson().getAccountStatus());
            personRepository.save(existingPerson);
        }

        // 2. อัปเดตข้อมูล Housekeeper หลัก
        existingHousekeeper.setStatusVerify(Housekeeper.VerifyStatus.valueOf(housekeeperDto.getStatusVerify()));
        existingHousekeeper.setDailyRate(housekeeperDto.getDailyRate());
        // อัปเดตฟิลด์อื่นๆ เช่น balance ถ้าจำเป็น

        Housekeeper updatedHousekeeper = housekeeperRepository.save(existingHousekeeper);
        Housekeeper transformedHousekeeper = transformHousekeeperUrls(updatedHousekeeper);

        return housekeeperMapper.toDto(transformedHousekeeper);
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
        Housekeeper housekeeper = housekeeperRepository.findById(housekeeperId)
                .orElseThrow(() -> new HousekeeperNotFoundException("Housekeeper with ID " + housekeeperId + " not found."));

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
        Housekeeper housekeeper = housekeeperRepository.findById(housekeeperId)
                .orElseThrow(() -> new HousekeeperNotFoundException("Housekeeper with ID " + housekeeperId + " not found."));

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
    public List<HousekeeperDTO> getHousekeepersByStatus(String status) {
        List<Housekeeper> entities = housekeeperRepository.findByStatusVerifyWithDetails(status);

        return entities.stream()
                .map(this::transformHousekeeperUrls)
                .map(housekeeperMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousekeeperDTO> getNotVerifiedOrNullStatusHousekeepers() {
        List<Housekeeper> entities = housekeeperRepository.findNotVerifiedOrNullStatusHousekeepersWithDetails();

        return entities.stream()
                .map(this::transformHousekeeperUrls)
                .map(housekeeperMapper::toDto)
                .collect(Collectors.toList());
    }
}