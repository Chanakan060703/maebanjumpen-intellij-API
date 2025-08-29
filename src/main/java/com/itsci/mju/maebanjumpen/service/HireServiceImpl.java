package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException;
import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException;
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.repository.HireRepository;
import com.itsci.mju.maebanjumpen.model.Hirer;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.repository.HirerRepository;
import com.itsci.mju.maebanjumpen.repository.HousekeeperRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate; // ** เพิ่ม import นี้ **

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Service
public class HireServiceImpl implements HireService {

    @Autowired
    private HireRepository hireRepository;

    @Autowired
    private HirerService hirerService;
    @Autowired
    private HousekeeperService housekeeperService;

    @Autowired
    private HirerRepository hirerRepository;
    @Autowired
    private HousekeeperRepository housekeeperRepository;


    @Override
    @Transactional(readOnly = true)
    public List<Hire> getAllHires() {
        return hireRepository.findAllWithDetails();
    }

    @Override
    @Transactional(readOnly = true)
    public Hire getHireById(Integer id) {
        return hireRepository.fetchByIdWithAllDetails(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hire> getHiresByHirerId(Integer hirerId) {
        return hireRepository.findByHirerIdWithDetails(hirerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hire> getHiresByHousekeeperId(Integer housekeeperId) {
        return hireRepository.findByHousekeeperIdWithDetails(housekeeperId);
    }

    @Override
    @Transactional
    public Hire saveHire(Hire hire) {
        if (hire.getHirer() == null || hire.getHirer().getId() == null) {
            throw new IllegalArgumentException("Hirer ID is required for creating a hire.");
        }
        Hirer hirer = hirerRepository.findById(hire.getHirer().getId())
                .orElseThrow(() -> new HirerNotFoundException("Hirer with ID " + hire.getHirer().getId() + " not found."));
        hire.setHirer(hirer);

        if (hire.getHousekeeper() == null || hire.getHousekeeper().getId() == null) {
            throw new IllegalArgumentException("Housekeeper ID is required for creating a hire.");
        }
        Housekeeper housekeeper = housekeeperRepository.findById(hire.getHousekeeper().getId())
                .orElseThrow(() -> new HousekeeperNotFoundException("Housekeeper with ID " + hire.getHousekeeper().getId() + " not found."));
        hire.setHousekeeper(housekeeper);


        Hire savedHire = hireRepository.save(hire);


        return hireRepository.fetchByIdWithAllDetails(savedHire.getHireId()).orElse(savedHire);
    }

    @Override
    @Transactional
    public Hire updateHire(Integer id, Hire hireDetailsFromClient) throws InsufficientBalanceException, HirerNotFoundException {
        Optional<Hire> existingHireOptional = hireRepository.fetchByIdWithAllDetails(id);

        if (existingHireOptional.isEmpty()) {
            return null;
        }

        Hire existingHire = existingHireOptional.get();
        String oldStatus = existingHire.getJobStatus();
        String newStatus = hireDetailsFromClient.getJobStatus();

        if ("Completed".equalsIgnoreCase(newStatus) && !"Completed".equalsIgnoreCase(oldStatus)) {
            if (existingHire.getHirer() == null || existingHire.getHirer().getId() == null) {
                throw new IllegalStateException("Missing hirer details for hire ID: " + id);
            }
            if (existingHire.getHousekeeper() == null || existingHire.getHousekeeper().getId() == null) {
                throw new IllegalStateException("Missing housekeeper details for hire ID: " + id);
            }
            if (existingHire.getPaymentAmount() == null) {
                throw new IllegalStateException("Missing payment amount for hire ID: " + id);
            }

            try {
                System.out.println("Attempting to deduct " + existingHire.getPaymentAmount() + " from Hirer ID: " + existingHire.getHirer().getId());
                hirerService.deductBalance(existingHire.getHirer().getId(), existingHire.getPaymentAmount());
                System.out.println("Deducted successfully from Hirer ID: " + existingHire.getHirer().getId());

                System.out.println("Attempting to add " + existingHire.getPaymentAmount() + " to Housekeeper ID: " + existingHire.getHousekeeper().getId());
                housekeeperService.addBalance(existingHire.getHousekeeper().getId(), existingHire.getPaymentAmount());
                System.out.println("Added successfully to Housekeeper ID: " + existingHire.getHousekeeper().getId());

            } catch (InsufficientBalanceException e) {
                System.err.println("Transaction failed: Insufficient balance for hirer " + existingHire.getHirer().getId() + " on hire " + id + ": " + e.getMessage());
                throw e;
            } catch (HirerNotFoundException e) {
                System.err.println("Transaction failed: Hirer " + existingHire.getHirer().getId() + " not found on hire " + id + ": " + e.getMessage());
                throw e;
            } catch (HousekeeperNotFoundException e) {
                System.err.println("Transaction failed: Housekeeper " + existingHire.getHousekeeper().getId() + " not found on hire " + id + ": " + e.getMessage());
                throw e;
            }
        }
        else if ("Cancelled".equalsIgnoreCase(newStatus) && !"Cancelled".equalsIgnoreCase(oldStatus)) {
            System.out.println("Job " + id + " status changed to Cancelled. Implement refund logic if applicable.");
        }

        existingHire.setJobStatus(newStatus);
        existingHire.setHireName(hireDetailsFromClient.getHireName());
        existingHire.setHireDetail(hireDetailsFromClient.getHireDetail());
        existingHire.setPaymentAmount(hireDetailsFromClient.getPaymentAmount());
        existingHire.setHireDate(hireDetailsFromClient.getHireDate());
        existingHire.setStartDate(hireDetailsFromClient.getStartDate());
        existingHire.setStartTime(hireDetailsFromClient.getStartTime());
        existingHire.setEndTime(hireDetailsFromClient.getEndTime());
        existingHire.setLocation(hireDetailsFromClient.getLocation());

        // ** แก้ไข: ลบโค้ดส่วนนี้ออกเพื่อป้องกันการ Overwrite **
        // if (hireDetailsFromClient.getProgressionImageUrls() != null) {
        //     existingHire.setProgressionImageUrls(hireDetailsFromClient.getProgressionImageUrls());
        //     Hibernate.initialize(existingHire.getProgressionImageUrls());
        // }

        Hire updatedHire = hireRepository.save(existingHire);
        return hireRepository.fetchByIdWithAllDetails(updatedHire.getHireId()).orElse(updatedHire);
    }

    @Override
    @Transactional
    public void deleteHire(Integer id) {
        hireRepository.deleteById(id);
    }
    @Override
    @Transactional
    public Hire addProgressionImagesToHire(Integer hireId, List<String> imageUrls) {
        // ดึงข้อมูล Hire แบบครบถ้วน เพื่อให้แน่ใจว่า progressionImageUrls ถูกโหลดมา
        Hire hire = hireRepository.fetchByIdWithAllDetails(hireId)
                .orElseThrow(() -> new IllegalArgumentException("Hire with ID " + hireId + " not found."));

        // เพิ่ม URL ใหม่เข้าไปใน Collection เดิม
        if (hire.getProgressionImageUrls() == null) {
            hire.setProgressionImageUrls(new ArrayList<>());
        }
        hire.getProgressionImageUrls().addAll(imageUrls);

        // บันทึกการเปลี่ยนแปลง
        return hireRepository.save(hire);
    }
}