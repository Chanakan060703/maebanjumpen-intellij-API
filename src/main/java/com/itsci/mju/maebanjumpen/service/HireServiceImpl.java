package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.HireDTO;
import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException;
import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException;
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException;
import com.itsci.mju.maebanjumpen.mapper.HireMapper;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.model.Hirer;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.HousekeeperSkill;
import com.itsci.mju.maebanjumpen.model.SkillType;
import com.itsci.mju.maebanjumpen.repository.HireRepository;
import com.itsci.mju.maebanjumpen.repository.HirerRepository;
import com.itsci.mju.maebanjumpen.repository.HousekeeperRepository;
import com.itsci.mju.maebanjumpen.repository.SkillTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HireServiceImpl implements HireService {

    private final HireMapper hireMapper;
    private final HireRepository hireRepository;
    private final HirerService hirerService;
    private final HousekeeperService housekeeperService;
    private final HousekeeperSkillService housekeeperSkillService;
    // private final SkillTypeMapper skillTypeMapper; // ⬅️ ไม่ได้ใช้ ถูกลบ
    private final SkillTypeRepository skillTypeRepository;
    private final HirerRepository hirerRepository;
    private final HousekeeperRepository housekeeperRepository;

    // ⚠️ ไม่ต้องประกาศ HirerMapper, HousekeeperMapper ที่นี่ เพราะ HireMapper จัดการ Nested DTOs เอง

    @Override
    @Transactional(readOnly = true)
    public List<HireDTO> getAllHires() {
        List<Hire> entities = hireRepository.findAll();
        return hireMapper.toDtoList(entities); // ⬅️ แก้ไขชื่อเมธอดเป็น toDtoList
    }

    @Override
    @Transactional(readOnly = true)
    public HireDTO getHireById(Integer id) {
        Hire hire = hireRepository.fetchByIdWithAllDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Hire with ID " + id + " not found."));
        return hireMapper.toDto(hire);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HireDTO> getHiresByHirerId(Integer hirerId) {
        List<Hire> hires = hireRepository.findByHirerIdWithDetails(hirerId);
        return hireMapper.toDtoList(hires); // ⬅️ แก้ไขชื่อเมธอดเป็น toDtoList
    }

    @Override
    @Transactional(readOnly = true)
    public List<HireDTO> getHiresByHousekeeperId(Integer housekeeperId) {
        List<Hire> hires = hireRepository.findByHousekeeperIdWithDetails(housekeeperId);
        return hireMapper.toDtoList(hires); // ⬅️ แก้ไขชื่อเมธอดเป็น toDtoList
    }

    /**
     * 💡 NEW SERVICE METHOD: ดึงเฉพาะงานจ้างที่เสร็จสมบูรณ์ ('Completed') สำหรับ Housekeeper ID นั้นๆ
     * ใช้สำหรับคำนวณ 'Jobs Done' และดึง Reviews
     */
    @Override
    @Transactional(readOnly = true)
    public List<HireDTO> getCompletedHiresByHousekeeperId(Integer housekeeperId) {
        // สถานะที่ใช้ในการค้นหา
        final String COMPLETED_STATUS = "Completed";

        // 💡 เรียก Repository method ใหม่ที่ดึงรายละเอียด Review และอื่นๆ มาพร้อม
        List<Hire> completedHires = hireRepository.findByHousekeeperIdAndJobStatusWithDetails(housekeeperId, COMPLETED_STATUS);

        // Map Entity ไป DTO และส่งกลับ
        return hireMapper.toDtoList(completedHires);
    }

    @Override
    @Transactional
    public HireDTO saveHire(HireDTO hireDto) {
        // 1. แปลง DTO เป็น Entity ชั่วคราวเพื่อเข้าถึง ID
        Hire hire = hireMapper.toEntity(hireDto);

        // 2. ดึง Entity ที่สมบูรณ์จาก ID ใน DTO (ใช้ตรรกะเดิม)
        Hirer hirer = validateAndGetHirer(hire);
        hire.setHirer(hirer);

        Housekeeper housekeeper = validateAndGetHousekeeper(hire);
        hire.setHousekeeper(housekeeper);

        SkillType skillType = validateAndGetSkillType(hire);
        hire.setSkillType(skillType);

        // 3. คำนวณราคารวม
        // ⚠️ แก้ไข: ต้องใช้ getter ที่ถูกต้องจาก DTO
        double totalPayment = calculateTotalPayment(housekeeper, skillType, hireDto.getAdditionalSkillTypeIds());
        hire.setPaymentAmount(totalPayment);

        // 4. ตรวจสอบ balance (ตรรกะเดิม)
        if (hirer.getBalance() < hire.getPaymentAmount()) {
            throw new InsufficientBalanceException("Insufficient balance to create a hire.");
        }

        // 5. ตั้งค่า Hire Name
        hire.setHireName(skillType.getSkillTypeName());

        // 6. สร้าง Hire Detail ใหม่ (ใช้ตรรกะเดิม)
        StringBuilder hireDetailBuilder = new StringBuilder();
        if (hireDto.getHireDetail() != null && !hireDto.getHireDetail().isEmpty()) {
            hireDetailBuilder.append(hireDto.getHireDetail());
        }

        if (hireDto.getAdditionalSkillTypeIds() != null && !hireDto.getAdditionalSkillTypeIds().isEmpty()) {
            String additionalSkillsString = hireDto.getAdditionalSkillTypeIds().stream()
                    .map(id -> skillTypeRepository.findById(id).map(SkillType::getSkillTypeName).orElse(null))
                    .filter(name -> name != null)
                    .collect(Collectors.joining(", "));

            if (!additionalSkillsString.isEmpty()) {
                if (hireDetailBuilder.length() > 0) {
                    hireDetailBuilder.append("\n");
                }
                hireDetailBuilder.append("Additional Services: ").append(additionalSkillsString);
            }
        }
        hire.setHireDetail(hireDetailBuilder.toString());

        // 7. บันทึกและคืนค่า DTO
        Hire savedHire = hireRepository.save(hire);
        Hire finalHire = hireRepository.fetchByIdWithAllDetails(savedHire.getHireId()).orElse(savedHire);
        return hireMapper.toDto(finalHire);
    }

    @Override
    @Transactional
    public HireDTO updateHire(Integer id, HireDTO hireDto)
            throws InsufficientBalanceException, HirerNotFoundException {
        // ... (โค้ด updateHire ที่เหลือถูกต้องแล้ว) ...
        Hire existingHire = hireRepository.fetchByIdWithAllDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Hire with ID " + id + " not found."));

        String oldStatus = existingHire.getJobStatus();
        String newStatus = hireDto.getJobStatus();

        if (newStatus != null && "Completed".equalsIgnoreCase(newStatus)
                && !"Completed".equalsIgnoreCase(oldStatus)) {
            // ดำเนินการหัก/เพิ่มยอดคงเหลือ และอัปเดตระดับทักษะ
            hirerService.deductBalance(existingHire.getHirer().getId(), existingHire.getPaymentAmount());
            housekeeperService.addBalance(existingHire.getHousekeeper().getId(), existingHire.getPaymentAmount());

            housekeeperSkillService.updateSkillLevelAndHiresCompleted(
                    existingHire.getHousekeeper().getId(),
                    existingHire.getSkillType().getSkillTypeId()
            );
        }

        if (newStatus != null) existingHire.setJobStatus(newStatus);
        if (hireDto.getHireName() != null) existingHire.setHireName(hireDto.getHireName());
        if (hireDto.getHireDetail() != null) existingHire.setHireDetail(hireDto.getHireDetail());
        if (hireDto.getHireDate() != null) existingHire.setHireDate(hireDto.getHireDate());
        if (hireDto.getStartDate() != null) existingHire.setStartDate(hireDto.getStartDate());
        if (hireDto.getStartTime() != null) existingHire.setStartTime(hireDto.getStartTime());
        if (hireDto.getEndTime() != null) existingHire.setEndTime(hireDto.getEndTime());
        if (hireDto.getLocation() != null) existingHire.setLocation(hireDto.getLocation());

        if (hireDto.getSkillType() != null
                && hireDto.getSkillType().getSkillTypeId() != null
                && !existingHire.getSkillType().getSkillTypeId()
                .equals(hireDto.getSkillType().getSkillTypeId())) {
            SkillType newSkillType = skillTypeRepository.findById(
                    hireDto.getSkillType().getSkillTypeId()
            ).orElseThrow(() ->
                    new IllegalArgumentException("SkillType with ID "
                            + hireDto.getSkillType().getSkillTypeId() + " not found."));
            existingHire.setSkillType(newSkillType);
        }

        Hire updatedHire = hireRepository.save(existingHire);
        Hire finalHire = hireRepository.fetchByIdWithAllDetails(updatedHire.getHireId()).orElse(updatedHire);
        return hireMapper.toDto(finalHire);
    }

    @Override
    @Transactional
    public void deleteHire(Integer id) {
        hireRepository.deleteById(id);
    }

    @Override
    @Transactional
    public HireDTO addProgressionImagesToHire(Integer hireId, List<String> imageUrls) {
        Hire hire = hireRepository.fetchByIdWithAllDetails(hireId)
                .orElseThrow(() -> new IllegalArgumentException("Hire with ID " + hireId + " not found."));
        if (hire.getProgressionImageUrls() == null) hire.setProgressionImageUrls(new ArrayList<>());
        hire.getProgressionImageUrls().addAll(imageUrls);

        Hire updatedHire = hireRepository.save(hire);
        return hireMapper.toDto(updatedHire);
    }

    // --- Helper methods (ทำงานกับ Entity) ---
    private double calculateTotalPayment(Housekeeper housekeeper, SkillType mainSkillType, List<Integer> additionalSkillTypeIds) {
        double totalPayment = 0.0;

        HousekeeperSkill mainSkill = housekeeper.getHousekeeperSkills().stream()
                .filter(s -> s.getSkillType().getSkillTypeId().equals(mainSkillType.getSkillTypeId()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Housekeeper " + housekeeper.getId()
                                + " does not have main skill type " + mainSkillType.getSkillTypeId() + "."));
        totalPayment += mainSkill.getPricePerDay();

        if (additionalSkillTypeIds != null && !additionalSkillTypeIds.isEmpty()) {
            for (Integer additionalSkillId : additionalSkillTypeIds) {
                HousekeeperSkill additionalSkill = housekeeper.getHousekeeperSkills().stream()
                        .filter(s -> s.getSkillType().getSkillTypeId().equals(additionalSkillId))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException("Housekeeper " + housekeeper.getId()
                                        + " does not have additional skill type " + additionalSkillId + "."));
                totalPayment += additionalSkill.getPricePerDay();
            }
        }
        return totalPayment;
    }

    private Hirer validateAndGetHirer(Hire hire) {
        if (hire.getHirer() == null || hire.getHirer().getId() == null) {
            throw new IllegalArgumentException("Hirer ID is required for creating a hire.");
        }
        return hirerRepository.findById(hire.getHirer().getId())
                .orElseThrow(() -> new HirerNotFoundException("Hirer with ID "
                        + hire.getHirer().getId() + " not found."));
    }

    private Housekeeper validateAndGetHousekeeper(Hire hire) {
        if (hire.getHousekeeper() == null || hire.getHousekeeper().getId() == null) {
            throw new IllegalArgumentException("Housekeeper ID is required for creating a hire.");
        }
        return housekeeperRepository.findById(hire.getHousekeeper().getId())
                .orElseThrow(() -> new HousekeeperNotFoundException("Housekeeper with ID "
                        + hire.getHousekeeper().getId() + " not found."));
    }

    private SkillType validateAndGetSkillType(Hire hire) {
        if (hire.getSkillType() == null || hire.getSkillType().getSkillTypeId() == null) {
            throw new IllegalArgumentException("SkillType ID is required for creating a hire.");
        }
        return skillTypeRepository.findById(hire.getSkillType().getSkillTypeId())
                .orElseThrow(() -> new IllegalArgumentException("SkillType with ID "
                        + hire.getSkillType().getSkillTypeId() + " not found."));
    }
}
