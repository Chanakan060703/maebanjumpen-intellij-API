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

    @Override
    @Transactional
    public PenaltyDTO savePenalty(PenaltyDTO penaltyDto) {
        Penalty penalty = penaltyMapper.toEntity(penaltyDto);
        Penalty savedPenalty = penaltyRepository.save(penalty);

        if (savedPenalty.getReport() != null && savedPenalty.getReport().getReportId() != null) {
            updateAccountStatusFromPenalty(savedPenalty);
        } else {
            System.err.println("Warning: Penalty saved but related Report ID is missing in the saved Entity. Account status was not updated.");
        }

        return penaltyMapper.toDto(savedPenalty);
    }

    @Override
    @Transactional
    public void deletePenalty(int id) {
        penaltyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PenaltyDTO updatePenalty(int id, PenaltyDTO penaltyDto) {
        Penalty existingPenalty = penaltyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Penalty not found with id: " + id));

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

        if (existingPenalty.getReport() != null && existingPenalty.getReport().getReportId() != null) {
            updateAccountStatusFromPenalty(updatedPenalty);
        }

        return penaltyMapper.toDto(updatedPenalty);
    }

    @Transactional
    private void updateAccountStatusFromPenalty(Penalty penalty) {
        // ✅ แก้ไข: ใช้ reportRepository.findById เพื่อดึง Report Entity ที่สมบูรณ์
        Optional<Report> optionalReport = reportRepository.findById(penalty.getReport().getReportId());

        if (optionalReport.isPresent()) {
            Report report = optionalReport.get();
            Person personToUpdate = null;

            if (report.getHousekeeper() != null && report.getHousekeeper().getPerson() != null) {
                personToUpdate = report.getHousekeeper().getPerson();
            } else if (report.getHirer() != null && report.getHirer().getPerson() != null) {
                personToUpdate = report.getHirer().getPerson();
            }

            if (personToUpdate != null) {
                personService.updateAccountStatus(personToUpdate.getPersonId(), penalty.getPenaltyType());
                System.out.println("Updated person account status to: " + penalty.getPenaltyType() + " for person ID: " + personToUpdate.getPersonId());
            } else {
                System.err.println("Error: Linked Person not found in Report ID: " + report.getReportId());
            }
        } else {
            System.err.println("Error: Report not found for Penalty ID: " + penalty.getPenaltyId());
        }
    }
}