package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.HireDTO;
import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException;
import com.itsci.mju.maebanjumpen.exception.HousekeeperNotFoundException;
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException;
import com.itsci.mju.maebanjumpen.mapper.HireMapper;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.model.Hirer;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
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

@Service
@RequiredArgsConstructor
public class HireServiceImpl implements HireService {

    private final HireMapper hireMapper;
    private final HireRepository hireRepository;
    private final HirerService hirerService;
    private final HousekeeperService housekeeperService;
    private final HousekeeperSkillService housekeeperSkillService;
    private final SkillTypeRepository skillTypeRepository;
    private final HirerRepository hirerRepository;
    private final HousekeeperRepository housekeeperRepository;

    // ⚠️ ไม่ต้องประกาศ HirerMapper, HousekeeperMapper ที่นี่ เพราะ HireMapper จัดการ Nested DTOs เอง

    @Override
    @Transactional(readOnly = true)
    public List<HireDTO> getAllHires() {
        List<Hire> entities = hireRepository.findAllWithDetails(); // 💡 ใช้ findAllWithDetails เพื่อโหลดรายละเอียด
        return hireMapper.toDtoList(entities);
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
        return hireMapper.toDtoList(hires);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HireDTO> getHiresByHousekeeperId(Integer housekeeperId) {
        List<Hire> hires = hireRepository.findByHousekeeperIdWithDetails(housekeeperId);
        return hireMapper.toDtoList(hires);
    }

    /**
     * 💡 NEW SERVICE METHOD: ดึงเฉพาะงานจ้างที่เสร็จสมบูรณ์ ('Completed') สำหรับ Housekeeper ID นั้นๆ
     * ใช้สำหรับคำนวณ 'Jobs Done' และดึง Reviews
     */
    @Override
    @Transactional(readOnly = true)
    public List<HireDTO> getCompletedHiresByHousekeeperId(Integer housekeeperId) {
        final String COMPLETED_STATUS = "Completed";
        List<Hire> completedHires = hireRepository.findByHousekeeperIdAndJobStatusWithDetails(housekeeperId, COMPLETED_STATUS);
        return hireMapper.toDtoList(completedHires);
    }

    @Override
    @Transactional
    public HireDTO saveHire(HireDTO hireDto) {
        // 1. แปลง DTO เป็น Entity ชั่วคราวเพื่อเข้าถึง ID
        Hire hire = hireMapper.toEntity(hireDto);

        // 2. ดึง Entity ที่สมบูรณ์จาก ID ใน DTO
        Hirer hirer = validateAndGetHirer(hire);
        hire.setHirer(hirer);

        Housekeeper housekeeper = validateAndGetHousekeeper(hire);
        hire.setHousekeeper(housekeeper);

        SkillType skillType = validateAndGetSkillType(hire);
        hire.setSkillType(skillType);

        // 3. กำหนดราคารวม: ใช้ค่าที่ส่งมาจาก DTO โดยตรง (คำนวณจาก Flutter แล้ว)
        if (hireDto.getPaymentAmount() == null || hireDto.getPaymentAmount() <= 0) {
            throw new IllegalArgumentException("Payment amount is required and must be a positive value.");
        }
        hire.setPaymentAmount(hireDto.getPaymentAmount());

        // 4. ตรวจสอบ balance
        if (hirer.getBalance() < hire.getPaymentAmount()) {
            throw new InsufficientBalanceException("Insufficient balance to create a hire.");
        }

        // 5. ตั้งค่า Hire Name (จาก SkillType)
        hire.setHireName(skillType.getSkillTypeName());

        // 6. ตั้งค่า Hire Detail: ใช้ค่าที่ส่งมาจาก DTO โดยตรง (รวมบริการเสริมแล้ว)
        // ❌ ลบตรรกะการสร้างสตริงบริการเสริมออกจาก Back-end
        hire.setHireDetail(hireDto.getHireDetail());

        // 7. บันทึกและคืนค่า DTO
        Hire savedHire = hireRepository.save(hire);
        // ดึงข้อมูล Hire ที่ถูกบันทึกพร้อมรายละเอียดทั้งหมด
        Hire finalHire = hireRepository.fetchByIdWithAllDetails(savedHire.getHireId()).orElse(savedHire);
        return hireMapper.toDto(finalHire);
    }

    @Override
    @Transactional
    public HireDTO updateHire(Integer id, HireDTO hireDto)
            throws InsufficientBalanceException, HirerNotFoundException {

        Hire existingHire = hireRepository.fetchByIdWithAllDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Hire with ID " + id + " not found."));

        String oldStatus = existingHire.getJobStatus();
        String newStatus = hireDto.getJobStatus();

        // ตรรกะการทำธุรกรรมเมื่อสถานะเปลี่ยนเป็น 'Completed'
        if (newStatus != null && "Completed".equalsIgnoreCase(newStatus)
                && !"Completed".equalsIgnoreCase(oldStatus)) {

            if (existingHire.getPaymentAmount() == null || existingHire.getPaymentAmount() <= 0) {
                throw new IllegalStateException("Cannot complete hire. Payment amount is missing or invalid.");
            }

            // หักยอดคงเหลือผู้จ้าง
            hirerService.deductBalance(existingHire.getHirer().getId(), existingHire.getPaymentAmount());
            // เพิ่มยอดคงเหลือแม่บ้าน
            housekeeperService.addBalance(existingHire.getHousekeeper().getId(), existingHire.getPaymentAmount());

            // อัปเดตระดับทักษะและจำนวนงานที่เสร็จสมบูรณ์ของแม่บ้าน
            housekeeperSkillService.updateSkillLevelAndHiresCompleted(
                    existingHire.getHousekeeper().getId(),
                    existingHire.getSkillType().getSkillTypeId()
            );
        }

        // อัปเดตฟิลด์อื่นๆ
        if (newStatus != null) existingHire.setJobStatus(newStatus);
        if (hireDto.getHireName() != null) existingHire.setHireName(hireDto.getHireName());
        if (hireDto.getHireDetail() != null) existingHire.setHireDetail(hireDto.getHireDetail());
        if (hireDto.getHireDate() != null) existingHire.setHireDate(hireDto.getHireDate());
        if (hireDto.getStartDate() != null) existingHire.setStartDate(hireDto.getStartDate());
        if (hireDto.getStartTime() != null) existingHire.setStartTime(hireDto.getStartTime());
        if (hireDto.getEndTime() != null) existingHire.setEndTime(hireDto.getEndTime());
        if (hireDto.getLocation() != null) existingHire.setLocation(hireDto.getLocation());

        // อัปเดต SkillType ถ้ามีการเปลี่ยนแปลง
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
        // ดึงข้อมูล Hire ที่ถูกอัปเดตพร้อมรายละเอียดทั้งหมด
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