package com.itsci.mju.maebanjumpen.penalty.service.impl;

import com.itsci.mju.maebanjumpen.penalty.dto.PenaltyDTO;
import com.itsci.mju.maebanjumpen.mapper.PenaltyMapper;
import com.itsci.mju.maebanjumpen.entity.Penalty;
import com.itsci.mju.maebanjumpen.entity.Person;
import com.itsci.mju.maebanjumpen.entity.Report;
import com.itsci.mju.maebanjumpen.entity.PartyRole; // 💡 ต้องใช้ PartyRole
import com.itsci.mju.maebanjumpen.partyrole.repository.PartyRoleRepository; // ✅ เพิ่ม Dependency ใหม่
import com.itsci.mju.maebanjumpen.penalty.repository.PenaltyRepository;
import com.itsci.mju.maebanjumpen.report.repository.ReportRepository;
import com.itsci.mju.maebanjumpen.penalty.service.PenaltyService;
import com.itsci.mju.maebanjumpen.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyMapper penaltyMapper;
    private final PenaltyRepository penaltyRepository;
    private final ReportRepository reportRepository;
    private final PersonService personService;
    private final PartyRoleRepository partyRoleRepository; // ✅ เพิ่ม Dependency

    @Override
    public List<PenaltyDTO> getAllPenalties() {
        List<Penalty> penalties = penaltyRepository.findAll();
        return penaltyMapper.toDtoList(penalties);
    }

    @Override
    public PenaltyDTO getPenaltyById(int id) {
        return penaltyRepository.findById(id)
                .map(penaltyMapper::toDto)
                .orElse(null);
    }

    /**
     * 🛑 **คำเตือน:** เมธอดนี้ถูกแทนที่ด้วย savePenalty(PenaltyDTO, Integer targetRoleId) แล้ว
     * กรุณาอัปเดต Controller ให้เรียกเมธอดใหม่
     */
    @Override
    @Transactional
    public PenaltyDTO savePenalty(PenaltyDTO penaltyDto) {
        throw new UnsupportedOperationException("Method savePenalty(PenaltyDTO) is deprecated. Use savePenalty(PenaltyDTO, Integer targetRoleId) instead.");
    }

    /**
     * ✅ **เมธอดที่แก้ไข:** บันทึกข้อมูลการลงโทษ และอัปเดตสถานะบัญชีของผู้ถูกลงโทษ
     * @param penaltyDto ข้อมูลการลงโทษ (มี reportId สำหรับผูกความสัมพันธ์)
     * @param targetRoleId ID ของบทบาท (Role ID เช่น 3 สำหรับ Hirer) ที่ต้องการลงโทษ
     * @return PenaltyDTO ที่ถูกบันทึก
     */
    @Transactional
    public PenaltyDTO savePenalty(PenaltyDTO penaltyDto, Integer targetRoleId) { // 💡 เปลี่ยน Signature
        if (targetRoleId == null) {
            throw new IllegalArgumentException("Target Role ID is required to apply penalty.");
        }

        // 1. Convert DTO to Entity and save the Penalty
        Penalty penalty = penaltyMapper.toEntity(penaltyDto);
        Penalty savedPenalty = penaltyRepository.save(penalty);

        // 2. Link Penalty to the owning Report Entity and update account status
        Integer reportId = penaltyDto.getReportId();
        if (reportId != null) {
            Optional<Report> optionalReport = reportRepository.findById(reportId);

            if (optionalReport.isPresent()) {
                Report report = optionalReport.get();

                // 2a. Set the new penalty on the Report (Report is the owning side)
                report.setPenalty(savedPenalty);
                // 2b. Update the report status to resolved/penalized
                report.setReportStatus("RESOLVED"); // ✅ เมื่อมีการลงโทษ ถือว่ารายงานถูกจัดการแล้ว
                reportRepository.save(report);

                // 2c. Update the account status of the penalized person
                updateAccountStatus(targetRoleId, savedPenalty.getPenaltyType()); // ✅ ใช้เมธอดใหม่
            } else {
                System.err.println("Warning: Report ID " + reportId + " not found for new Penalty. Linking skipped.");
            }
        } else {
            System.err.println("Error: reportId is missing from PenaltyDTO. Cannot link Penalty to Report or update account status.");
        }

        return penaltyMapper.toDto(savedPenalty);
    }

    /**
     * ✅ **เมธอดใหม่:** ดึง Person ID จาก Role ID และอัปเดตสถานะบัญชี
     * 🎯 แก้ไข Logic การระบุตัวผู้ถูกลงโทษโดยตรง โดยใช้ targetRoleId
     * @param targetRoleId ID ของบทบาท (Role ID) ที่ต้องการลงโทษ
     * @param penaltyType ประเภทการลงโทษ (สถานะใหม่ของบัญชี)
     */
    @Transactional
    private void updateAccountStatus(Integer targetRoleId, String penaltyType) {
        // 1. ค้นหา PartyRole (Hirer/Housekeeper) จาก Role ID ที่ Frontend ระบุ
        // NOTE: ต้องมั่นใจว่า PartyRole Entity โหลด Person object มาด้วย (lazy load อาจต้องการ @Transactional)
        Optional<PartyRole> optionalPartyRole = partyRoleRepository.findById(targetRoleId);

        if (optionalPartyRole.isPresent()) {
            PartyRole partyRole = optionalPartyRole.get();
            Person personToUpdate = partyRole.getPerson();

            if (personToUpdate != null) {
                personService.updateAccountStatus(personToUpdate.getPersonId(), penaltyType);
                System.out.println("Updated person account status to: " + penaltyType + " for person ID: " + personToUpdate.getPersonId());
            } else {
                System.err.println("Error: Person object is missing for Role ID: " + targetRoleId + ". Cannot update account status.");
            }
        } else {
            System.err.println("Error: Target PartyRole not found with ID: " + targetRoleId + ". Cannot apply penalty.");
        }
    }


    /**
     * ลบ Penalty และยกเลิกการผูกความสัมพันธ์กับ Report
     * @param id ID ของ Penalty
     */
    @Override
    @Transactional
    public void deletePenalty(int id) {
        Optional<Penalty> optionalPenalty = penaltyRepository.findById(id);

        if (optionalPenalty.isPresent()) {
            Penalty penaltyToDelete = optionalPenalty.get();

            // 1. Find the related Report (Report is the owning side)
            if (penaltyToDelete.getReport() != null && penaltyToDelete.getReport().getReportId() != null) {
                reportRepository.findById(penaltyToDelete.getReport().getReportId()).ifPresent(report -> {
                    // 2. Unlink the Penalty from the Report (Owning side)
                    report.setPenalty(null);
                    report.setReportStatus("RESOLVED");
                    reportRepository.save(report);

                    // 3. Revert Account Status (TODO: ตรรกะการยกเลิกโทษ)
                    System.out.println("Penalty ID " + id + " was unlinked from Report ID " + report.getReportId());
                });
            }
            // 4. Delete the Penalty
            penaltyRepository.delete(penaltyToDelete);
        }
    }

    /**
     * อัปเดตข้อมูลการลงโทษ
     * 🛑 **คำเตือน:** เมธอดนี้ยังไม่สามารถอัปเดตสถานะบัญชีได้อย่างปลอดภัย (ดู Warning ด้านล่าง)
     */
    @Override
    @Transactional
    public PenaltyDTO updatePenalty(int id, PenaltyDTO penaltyDto) {
        Penalty existingPenalty = penaltyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Penalty not found with id: " + id));

        // ตรวจสอบว่ามีการเปลี่ยนแปลงประเภทโทษหรือไม่
        boolean penaltyTypeChanged = penaltyDto.getPenaltyType() != null &&
                !existingPenalty.getPenaltyType().equals(penaltyDto.getPenaltyType());

        // อัปเดตข้อมูล
        if (penaltyDto.getPenaltyType() != null) {
            existingPenalty.setPenaltyType(penaltyDto.getPenaltyType());
        }
        if (penaltyDto.getPenaltyDetail() != null) {
            existingPenalty.setPenaltyDetail(penaltyDto.getPenaltyDetail());
        }
        if (penaltyDto.getPenaltyDate() != null) {
            existingPenalty.setPenaltyDate(penaltyDto.getPenaltyDate());
        }
        if (penaltyDto.getPenaltyStatus() != null) {
            existingPenalty.setPenaltyStatus(penaltyDto.getPenaltyStatus());
        }

        Penalty updatedPenalty = penaltyRepository.save(existingPenalty);

        // ถ้ามีการเปลี่ยนประเภทโทษ อัปเดตสถานะบัญชี
        if (penaltyTypeChanged) {
            // 🛑 WARNING: Logic เดิมไม่สามารถระบุ Target ได้ชัดเจนจาก Penalty Entity
            // ซึ่งต้องแก้ไขโดยการส่ง Target Role ID เข้ามาใน DTO สำหรับการอัปเดตด้วย
            System.err.println("Warning: Skipping account status update in updatePenalty method because the target person ID cannot be reliably determined from the existing entities.");
        }

        return penaltyMapper.toDto(updatedPenalty);
    }
}
