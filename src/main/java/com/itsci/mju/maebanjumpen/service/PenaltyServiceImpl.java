package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.PenaltyDTO;
import com.itsci.mju.maebanjumpen.mapper.PenaltyMapper;
import com.itsci.mju.maebanjumpen.model.Penalty;
import com.itsci.mju.maebanjumpen.model.Person;
import com.itsci.mju.maebanjumpen.model.Report;
import com.itsci.mju.maebanjumpen.repository.PenaltyRepository;
import com.itsci.mju.maebanjumpen.repository.ReportRepository;
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
     * บันทึกข้อมูลการลงโทษ และอัปเดตสถานะบัญชีของผู้ถูกลงโทษ
     * พร้อมทั้งผูก Penalty Entity เข้ากับ Report Entity ที่เกี่ยวข้อง (Report คือ Owning Side)
     * @param penaltyDto ข้อมูลการลงโทษ
     * @return PenaltyDTO ที่ถูกบันทึก
     */
    @Override
    @Transactional
    public PenaltyDTO savePenalty(PenaltyDTO penaltyDto) {
        // 1. Convert DTO to Entity and save the Penalty (Penalty is the inverse side)
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
                // 2b. Update the report status to reflect the penalty has been applied
                report.setReportStatus("PENALIZED");
                reportRepository.save(report);

                // 2c. Update the account status of the penalized person
                updateAccountStatusFromReport(report, savedPenalty.getPenaltyType());
            } else {
                System.err.println("Warning: Report ID " + reportId + " not found for new Penalty. Linking skipped.");
            }
        } else {
            System.err.println("Error: reportId is missing from PenaltyDTO. Cannot link Penalty to Report or update account status.");
        }

        return penaltyMapper.toDto(savedPenalty);
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
            // Note: Accessing penaltyToDelete.getReport() can trigger LAZY loading if done in a separate session
            // We use the ID to ensure we retrieve a fresh, managed entity if needed.
            // Since this is @Transactional, direct access should be okay, but using reportRepository.findById is safer for explicit fetching.
            if (penaltyToDelete.getReport() != null && penaltyToDelete.getReport().getReportId() != null) {
                reportRepository.findById(penaltyToDelete.getReport().getReportId()).ifPresent(report -> {
                    // 2. Unlink the Penalty from the Report (Owning side)
                    report.setPenalty(null);
                    report.setReportStatus("RESOLVED"); // หรือสถานะที่เหมาะสมหลังยกเลิกโทษ
                    reportRepository.save(report);

                    // 3. Revert Account Status (TODO: ตรรกะการยกเลิกโทษ)
                    // ตัวอย่าง: ถ้า PenaltyType เดิมคือ BANNED อาจเปลี่ยนเป็น ACTIVE
                    // หากไม่มีตรรกะยกเลิกที่ชัดเจน ให้บันทึก log ไว้
                    System.out.println("Penalty ID " + id + " was unlinked from Report ID " + report.getReportId());
                });
            }
            // 4. Delete the Penalty
            penaltyRepository.delete(penaltyToDelete);
        }
    }

    /**
     * อัปเดตข้อมูลการลงโทษ และอัปเดตสถานะบัญชีของผู้ถูกลงโทษ หากมีการเปลี่ยนแปลงประเภทโทษ
     * @param id ID ของ Penalty
     * @param penaltyDto ข้อมูลการลงโทษที่ต้องการอัปเดต
     * @return PenaltyDTO ที่ถูกอัปเดต
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
            // ดึง Report มาเพื่ออัปเดตสถานะบัญชี
            if (updatedPenalty.getReport() != null && updatedPenalty.getReport().getReportId() != null) {
                reportRepository.findById(updatedPenalty.getReport().getReportId()).ifPresent(report -> {
                    updateAccountStatusFromReport(report, updatedPenalty.getPenaltyType());
                });
            } else if (penaltyDto.getReportId() != null) {
                // กรณีที่ Penalty Entity อาจจะยังไม่ได้โหลด Report มา แต่ DTO มี reportId
                reportRepository.findById(penaltyDto.getReportId()).ifPresent(report -> {
                    updateAccountStatusFromReport(report, updatedPenalty.getPenaltyType());
                });
            }
        }

        return penaltyMapper.toDto(updatedPenalty);
    }

    /**
     * ดึง Report Entity ที่สมบูรณ์มา เพื่อค้นหา Person ที่ถูกลงโทษ และอัปเดตสถานะบัญชี
     * @param report Report Entity ที่เกี่ยวข้อง (ควรเป็น Fully Initialized Entity)
     * @param penaltyType ประเภทการลงโทษ (สถานะใหม่ของบัญชี)
     */
    @Transactional
    private void updateAccountStatusFromReport(Report report, String penaltyType) {
        Person personToUpdate = null;

        // ตรวจสอบว่าใครคือผู้ถูกลงโทษ (Housekeeper หรือ Hirer)
        /*
         * ตรรกะการระบุผู้ถูกลงโทษ:
         * 1. ตรวจสอบ Housekeeper ก่อน (มักจะถือว่าเป็นผู้ให้บริการหลัก)
         * 2. หาก Housekeeper ไม่มี (null) หรือข้อมูล Person ไม่สมบูรณ์ จึงตรวจสอบ Hirer
         * ในกรณีที่ Report มี Housekeeper ID เป็น null และมี Hirer ID (ตาม log: Hirer ID: 1, Housekeeper ID: null)
         * โค้ดจะข้ามเงื่อนไขแรกและเลือก Hirer ได้ถูกต้อง
         */
        if (report.getHousekeeper() != null && report.getHousekeeper().getPerson() != null) {
            personToUpdate = report.getHousekeeper().getPerson();
        } else if (report.getHirer() != null && report.getHirer().getPerson() != null) {
            personToUpdate = report.getHirer().getPerson();
        }

        if (personToUpdate != null) {
            personService.updateAccountStatus(personToUpdate.getPersonId(), penaltyType);
            System.out.println("Updated person account status to: " + penaltyType + " for person ID: " + personToUpdate.getPersonId());
        } else {
            System.err.println("Error: Linked Person (Hirer/Housekeeper) not found in Report ID: " + report.getReportId() + ". Cannot update account status. (Both Hirer and Housekeeper fields may be null or incomplete.)");
        }
    }
}
