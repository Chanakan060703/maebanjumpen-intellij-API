package com.itsci.mju.maebanjumpen.service.impl;

import com.itsci.mju.maebanjumpen.dto.HousekeeperDTO;
import com.itsci.mju.maebanjumpen.dto.HousekeeperDetailDTO;
import com.itsci.mju.maebanjumpen.dto.ReviewDTO;
import com.itsci.mju.maebanjumpen.dto.HireDTO;
import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException;
import com.itsci.mju.maebanjumpen.mapper.HousekeeperMapper;
import com.itsci.mju.maebanjumpen.mapper.PersonMapper;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.Person;
import com.itsci.mju.maebanjumpen.repository.HousekeeperRepository;
import com.itsci.mju.maebanjumpen.repository.PersonRepository;
import com.itsci.mju.maebanjumpen.service.HousekeeperService;
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

    private final HousekeeperMapper housekeeperMapper;
    private final HousekeeperRepository housekeeperRepository;
    private final PersonMapper personMapper;
    private final PersonRepository personRepository;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

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

    private void transformHireHirerUrls(List<HireDTO> hires) {
        if (hires == null) return;

        for (HireDTO hireDto : hires) {
            if (hireDto.getHirer() != null && hireDto.getHirer().getPerson() != null) {
                var hirerPersonDto = hireDto.getHirer().getPerson();
                String originalFilename = hirerPersonDto.getPictureUrl();
                String fullUrl = buildFullImageUrl(originalFilename, "profile_pictures");
                hirerPersonDto.setPictureUrl(fullUrl);
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<HousekeeperDTO> getAllHousekeepers() {
        List<Housekeeper> entities = housekeeperRepository.findAllWithPersonLoginAndSkills();
        return entities.stream()
                .map(this::transformHousekeeperUrls)
                .map(housekeeperMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HousekeeperDetailDTO getHousekeeperDetailById(int id) {
        Optional<Housekeeper> housekeeperOptional = housekeeperRepository.findByIdWithAllDetails(id);
        if (housekeeperOptional.isEmpty()) {
            return null;
        }
        Housekeeper housekeeper = housekeeperOptional.get();
        Housekeeper transformedHousekeeper = this.transformHousekeeperUrls(housekeeper);
        HousekeeperDetailDTO detailDto = housekeeperMapper.toDetailDto(transformedHousekeeper);


        if (detailDto.getHires() != null) {
            transformHireHirerUrls(detailDto.getHires());
        }


        List<ReviewDTO> reviews = Collections.emptyList();
        if (detailDto.getHires() != null) {
            reviews = detailDto.getHires().stream()
                    .map(HireDTO::getReview)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        }

        detailDto.setReviews(reviews);
        detailDto.setHires(null);

        return detailDto;
    }

    @Override
    @Transactional
    public HousekeeperDTO saveHousekeeper(HousekeeperDTO housekeeperDto) {
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

    @Override
    @Transactional
    public HousekeeperDTO updateHousekeeper(int id, HousekeeperDTO housekeeperDto) {
        Housekeeper existingHousekeeper = housekeeperRepository.findById(id)
                .orElseThrow(() -> new HousekeeperNotFoundException("Housekeeper with ID " + id + " not found."));

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

        existingHousekeeper.setStatusVerify(Housekeeper.VerifyStatus.valueOf(housekeeperDto.getStatusVerify()));
        existingHousekeeper.setDailyRate(housekeeperDto.getDailyRate());

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