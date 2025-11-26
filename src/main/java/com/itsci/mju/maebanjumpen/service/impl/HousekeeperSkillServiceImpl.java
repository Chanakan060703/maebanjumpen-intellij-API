package com.itsci.mju.maebanjumpen.service.impl;

import com.itsci.mju.maebanjumpen.dto.HousekeeperDTO;
import com.itsci.mju.maebanjumpen.dto.HousekeeperSkillDTO;
import com.itsci.mju.maebanjumpen.mapper.HousekeeperMapper;
import com.itsci.mju.maebanjumpen.mapper.HousekeeperSkillMapper;
import com.itsci.mju.maebanjumpen.mapper.SkillLevelTierMapper;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.HousekeeperSkill;
import com.itsci.mju.maebanjumpen.model.SkillLevelTier;
import com.itsci.mju.maebanjumpen.model.SkillType;
import com.itsci.mju.maebanjumpen.repository.HousekeeperRepository;
import com.itsci.mju.maebanjumpen.repository.HousekeeperSkillRepository;
import com.itsci.mju.maebanjumpen.repository.SkillLevelTierRepository;
import com.itsci.mju.maebanjumpen.repository.SkillTypeRepository;

import com.itsci.mju.maebanjumpen.service.HousekeeperSkillService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class HousekeeperSkillServiceImpl implements HousekeeperSkillService {

    private final HousekeeperMapper housekeeperMapper;
    private final HousekeeperSkillMapper housekeeperSkillMapper;
    private final SkillLevelTierMapper skillLevelTierMapper;
    private final HousekeeperSkillRepository housekeeperSkillRepository;
    private final SkillLevelTierRepository skillLevelTierRepository;
    private final HousekeeperRepository housekeeperRepository;
    private final SkillTypeRepository skillTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HousekeeperDTO> getAllHousekeeperSkills() {
        List<Housekeeper> housekeepers = housekeeperRepository.findAll();
        return housekeeperMapper.toDtoList(housekeepers);
    }

    @Override
    @Transactional(readOnly = true)
    public HousekeeperSkillDTO getHousekeeperSkillById(int id) {
        HousekeeperSkill entity = housekeeperSkillRepository.findById(id)
                .orElse(null);
        return housekeeperSkillMapper.toDto(entity);
    }

    @Override
    public HousekeeperSkillDTO saveHousekeeperSkill(HousekeeperSkillDTO housekeeperSkillDto) {
        Integer housekeeperId = housekeeperSkillDto.getHousekeeperId();
        Integer skillTypeId = housekeeperSkillDto.getSkillTypeId();

        // 1. ตรวจสอบว่าทักษะนี้มีอยู่แล้วสำหรับแม่บ้านคนนี้หรือไม่ (🎯 การแก้ไขหลัก)
        Optional<HousekeeperSkill> optionalExistingHs = housekeeperSkillRepository
                .findByHousekeeperIdAndSkillTypeSkillTypeId(housekeeperId, skillTypeId);

        if (optionalExistingHs.isPresent()) {
            // หากทักษะมีอยู่แล้ว: ป้องกันการบันทึกซ้ำ
            // เราจะทำการ update แทนการสร้างใหม่ หรือ throw Exception
            HousekeeperSkill existingSkill = optionalExistingHs.get();

            // อนุญาตให้อัปเดตราคา per day เท่านั้น ถ้ามีการส่งค่ามา (Upsert Behavior)
            if (housekeeperSkillDto.getPricePerDay() != null) {
                existingSkill.setPricePerDay(housekeeperSkillDto.getPricePerDay());
            }

            HousekeeperSkill updatedSkill = housekeeperSkillRepository.save(existingSkill);
            return housekeeperSkillMapper.toDto(updatedSkill);
            // หรือถ้าต้องการป้องกันการ "เพิ่ม" อย่างเดียว ให้ throw Exception:
            // throw new IllegalStateException("Housekeeper already has skill ID " + skillTypeId);
        }

        // 2. ถ้าทักษะยังไม่มี: ดำเนินการสร้าง HousekeeperSkill ใหม่ (โค้ดเดิม)
        HousekeeperSkill entity = housekeeperSkillMapper.toEntity(housekeeperSkillDto);

        Housekeeper housekeeper = housekeeperRepository.findById(housekeeperId)
                .orElseThrow(() -> new EntityNotFoundException("Housekeeper not found with ID: " + housekeeperId));
        entity.setHousekeeper(housekeeper);

        SkillType skillType = skillTypeRepository.findById(skillTypeId)
                .orElseThrow(() -> new EntityNotFoundException("SkillType not found with ID: " + skillTypeId));
        entity.setSkillType(skillType);

        // 3. กำหนดค่า SkillLevelTier (ใช้โค้ดหา Tier เริ่มต้น)
        SkillLevelTier skillLevelTier;
        if (housekeeperSkillDto.getSkillLevelTierId() != null) {
            skillLevelTier = skillLevelTierRepository.findById(housekeeperSkillDto.getSkillLevelTierId())
                    .orElseThrow(() -> new EntityNotFoundException("SkillLevelTier not found with ID: " + housekeeperSkillDto.getSkillLevelTierId()));
        } else {
            skillLevelTier = skillLevelTierRepository.findAll().stream()
                    .min(Comparator.comparing(SkillLevelTier::getMinHiresForLevel))
                    .orElseThrow(() -> new RuntimeException("No SkillLevelTier found. Cannot set initial level."));
        }

        entity.setSkillLevelTier(skillLevelTier);
        entity.setTotalHiresCompleted(0);

        // 4. บันทึก
        HousekeeperSkill savedEntity = housekeeperSkillRepository.save(entity);

        return housekeeperSkillMapper.toDto(savedEntity);
    }


    @Override
    public void deleteHousekeeperSkill(int id) {
        housekeeperSkillRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HousekeeperSkillDTO> getSkillsByHousekeeperId(int housekeeperId) {
        Optional<Housekeeper> optionalHousekeeper = housekeeperRepository.findById(housekeeperId);

        if (optionalHousekeeper.isPresent() && !optionalHousekeeper.get().getHousekeeperSkills().isEmpty()) {
            HousekeeperSkill firstSkill = optionalHousekeeper.get().getHousekeeperSkills().iterator().next();
            return Optional.of(housekeeperSkillMapper.toDto(firstSkill));
        }

        return Optional.empty();
    }

    @Override
    public HousekeeperSkillDTO updateHousekeeperSkill(int id, HousekeeperSkillDTO skillDto) {
        return housekeeperSkillRepository.findById(id).map(existingSkill -> {

            // อัปเดตราคาต่อวัน
            if (skillDto.getPricePerDay() != null) {
                existingSkill.setPricePerDay(skillDto.getPricePerDay());
            }

            // อัปเดตจำนวนครั้งที่ทำงานสำเร็จ (ถ้าจำเป็น)
            if (skillDto.getTotalHiresCompleted() != null) {
                existingSkill.setTotalHiresCompleted(skillDto.getTotalHiresCompleted());
            }

            // 💡 การแก้ไข: หากมีการส่ง skillLevelTierId มาใน DTO ให้ใช้ ID นั้นในการอัปเดต
            if (skillDto.getSkillLevelTierId() != null) {
                SkillLevelTier newTier = skillLevelTierRepository.findById(skillDto.getSkillLevelTierId())
                        .orElseThrow(() -> new EntityNotFoundException("SkillLevelTier not found with ID: " + skillDto.getSkillLevelTierId()));
                existingSkill.setSkillLevelTier(newTier);
            }
            // ⚠️ ส่วนของ skillDto.getSkillLevelTier() != null && skillDto.getSkillLevelTier().getId() != null ถูกแทนที่ด้วย Logic ด้านบนแล้ว

            HousekeeperSkill updatedSkill = housekeeperSkillRepository.save(existingSkill);
            return housekeeperSkillMapper.toDto(updatedSkill);

        }).orElseThrow(() -> new NoSuchElementException("HousekeeperSkill not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HousekeeperSkillDTO> findByHousekeeperIdAndSkillTypeId(Integer housekeeperId, Integer skillTypeId) {
        Optional<HousekeeperSkill> optionalHs = housekeeperSkillRepository.findByHousekeeperIdAndSkillTypeSkillTypeId(housekeeperId, skillTypeId);
        return optionalHs.map(housekeeperSkillMapper::toDto);
    }

    @Override
    public void updateSkillLevelAndHiresCompleted(Integer housekeeperId, Integer skillTypeId) {
        Optional<HousekeeperSkill> optionalHs = housekeeperSkillRepository.findByHousekeeperIdAndSkillTypeSkillTypeId(housekeeperId, skillTypeId);

        if (optionalHs.isPresent()) {
            HousekeeperSkill hs = optionalHs.get();
            hs.setTotalHiresCompleted(hs.getTotalHiresCompleted() + 1);
            recalculateSkillLevel(hs);
            housekeeperSkillRepository.save(hs);
        } else {
            System.err.println("HousekeeperSkill not found for housekeeper " + housekeeperId + " and skill " + skillTypeId);
        }
    }

    private void recalculateSkillLevel(HousekeeperSkill hs) {
        List<SkillLevelTier> tiers = skillLevelTierRepository.findAll();
        tiers.sort(Comparator.comparing(SkillLevelTier::getMinHiresForLevel).reversed());

        Integer currentHires = hs.getTotalHiresCompleted();
        SkillLevelTier newTier = hs.getSkillLevelTier();

        for (SkillLevelTier tier : tiers) {
            if (currentHires >= tier.getMinHiresForLevel()) {
                newTier = tier;
                break;
            }
        }

        if (newTier != null && !newTier.equals(hs.getSkillLevelTier())) {
            hs.setSkillLevelTier(newTier);
        }
    }
}