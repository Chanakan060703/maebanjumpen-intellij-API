package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Penalty;
import com.itsci.mju.maebanjumpen.model.Person; // นำเข้า Person model
import com.itsci.mju.maebanjumpen.repository.PenaltyRepository;
import com.itsci.mju.maebanjumpen.repository.ReportRepository; // อาจต้องใช้ ReportRepository ด้วย
import com.itsci.mju.maebanjumpen.repository.HirerRepository; // นำเข้า HirerRepository
import com.itsci.mju.maebanjumpen.repository.HousekeeperRepository; // นำเข้า HousekeeperRepository
import com.itsci.mju.maebanjumpen.model.Report; // นำเข้า Report model
import com.itsci.mju.maebanjumpen.model.Hirer; // นำเข้า Hirer model
import com.itsci.mju.maebanjumpen.model.Housekeeper; // นำเข้า Housekeeper model

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // เพิ่ม Transactional เพื่อความปลอดภัย

import java.util.List;
import java.util.Optional;

@Service
public class PenaltyServiceImpl implements PenaltyService {

    @Autowired
    private PenaltyRepository penaltyRepository;

    @Autowired
    private ReportRepository reportRepository; // เพิ่ม ReportRepository

    @Autowired
    private HirerRepository hirerRepository; // เพิ่ม HirerRepository

    @Autowired
    private HousekeeperRepository housekeeperRepository; // เพิ่ม HousekeeperRepository

    // PersonService ถูกนำไปใช้เพื่ออัปเดต accountStatus ของ Person
    // ถ้า PersonService ยังไม่มี ให้สร้างตามตัวอย่างด้านบน
    @Autowired
    private PersonService personService; // เพิ่ม PersonService

    @Override
    public List<Penalty> getAllPenalties() {
        return penaltyRepository.findAll();
    }

    @Override
    public Penalty getPenaltyById(int id) {
        return penaltyRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional // ทำให้ method นี้เป็นการทำ Transactional เพื่อให้แน่ใจว่าถ้ามีขั้นตอนใดล้มเหลวจะ rollback ทั้งหมด
    public Penalty savePenalty(Penalty penalty) {
        Penalty savedPenalty = penaltyRepository.save(penalty);

        // --- เพิ่ม Logic สำหรับอัปเดต accountStatus ของ Person ที่เกี่ยวข้อง ---
        // 1. หา Report ที่เกี่ยวข้องกับ Penalty นี้ เพื่อดูว่าเป็นการลงโทษใคร (Hirer หรือ Housekeeper)
        //    ในการเรียกใช้จาก Flutter คุณส่ง reportId มาด้วย แต่ใน signature ของ savePenalty
        //    ไม่มี reportId ดังนั้นเราอาจจะต้องปรับ Controller ให้ส่ง reportId มายัง Service ด้วย
        //    หรือหา Report จาก Frontend ที่ส่งมาพร้อม Penalty (ถ้า Penalty มี field สำหรับ reportId)

        // **สมมติฐาน:** Frontend ส่ง reportId มาให้ PenaltyController แล้ว Controller ส่งต่อ
        // reportId ไปยัง PenaltyService.savePenalty หรือคุณต้องดึง reportId จาก Penalty object
        // (ซึ่ง ณ ตอนนี้ Penalty model ของคุณยังไม่มี field reportId)

        // **วิธีที่ดีกว่า:** ให้ Controller ของ Penalty เป็นคนรับผิดชอบในการเชื่อมโยง Penalty กับ Report
        // และส่ง ID ของ Hirer/Housekeeper มาให้ Service หรือส่ง Report object เข้ามาเลย
        // แต่จาก Log ของคุณ, Flutter เรียก addPenalty ด้วย reportId และ hirerId/housekeeperId
        // ดังนั้นเราควรใช้ข้อมูลเหล่านี้ในการอัปเดตสถานะ

        // ***หากคุณส่ง reportId, hirerId, housekeeperId มายัง Service***
        // คุณอาจจะต้องปรับ method signature ของ savePenalty ให้รับ reportId, hirerId, housekeeperId ด้วย
        // เช่น public Penalty savePenalty(Penalty penalty, Integer reportId, Integer hirerId, Integer housekeeperId)

        // หรือถ้าคุณปรับ Flutter ให้ส่ง Report object พร้อมกับ Penalty ใน request เดียวกัน (ซึ่งอาจจะซับซ้อนไปหน่อย)

        // ***วิธีแก้ปัญหาชั่วคราว/แนวทางที่เข้ากับ Flutter log เดิม:***
        // เนื่องจาก log บอกว่า Penalty ถูกสร้างขึ้นก่อน แล้วค่อย update Report
        // ดังนั้น ตอนที่สร้าง Penalty เรายังไม่มีข้อมูล hirer/housekeeper ID ใน Penalty object โดยตรง
        // แต่ถ้าคุณต้องการให้ Penalty เป็นตัว Trigger การเปลี่ยนสถานะ accountStatus
        // คุณจะต้องส่งข้อมูลที่บอกว่าใครถูกลงโทษมากับ Penalty หรือหาจาก Report ใน Controller แล้วส่งมา Service

        // **ตัวอย่าง Logic ที่จะเพิ่มใน Service:**
        // (ในตัวอย่างนี้ ผมจะสมมติว่าคุณสามารถเข้าถึง reportId, hirerId, housekeeperId ได้จาก Controller
        // และส่งมายัง savePenalty method นี้ หรือสามารถดึงจาก database ได้)

        // หากคุณสามารถหา Report ID ได้:
        // Optional<Report> optionalReport = reportRepository.findById(reportId); // คุณต้องมี reportId ตรงนี้
        // if (optionalReport.isPresent()) {
        //     Report report = optionalReport.get();
        //     Person personToUpdate = null;
        //     if (report.getHirer() != null && report.getHirer().getPerson() != null) {
        //         personToUpdate = report.getHirer().getPerson();
        //     } else if (report.getHousekeeper() != null && report.getHousekeeper().getPerson() != null) {
        //         personToUpdate = report.getHousekeeper().getPerson();
        //     }
        //
        //     if (personToUpdate != null) {
        //         personToUpdate.setAccountStatus(penalty.getPenaltyType()); // ตั้งค่าสถานะตาม PenaltyType
        //         personService.savePerson(personToUpdate); // บันทึกการเปลี่ยนแปลงใน Person
        //         System.out.println("Updated person account status to: " + penalty.getPenaltyType() + " for person ID: " + personToUpdate.getPersonId());
        //     }
        // }


        // ***แนวทางที่เข้ากับ Log ปัจจุบันของคุณ (แต่ต้องมีการส่ง hirerId/housekeeperId จาก Controller มาที่ Service)***
        // ผมจะปรับปรุง signature ของ createPenalty ใน PenaltyController
        // และ PenaltyService.savePenalty เพื่อส่งข้อมูลผู้ถูกลงโทษมาด้วย
        // (ดูในส่วน `PenaltyController.java` ด้านล่าง)

        return savedPenalty;
    }

    @Override
    public void deletePenalty(int id) {
        penaltyRepository.deleteById(id);
    }

    @Override
    public Penalty updatePenalty(int id, Penalty penalty) {
        // ใน updatePenalty นี้ คุณอาจจะต้องเพิ่ม logic การ update accountStatus ด้วย
        // เช่นเดียวกับ savePenalty ถ้า penaltyType มีการเปลี่ยนแปลง
        Penalty existingPenalty = penaltyRepository.findById(id) // ใช้ findById แทน getReferenceById
                .orElseThrow(() -> new RuntimeException("Penalty not found with id: " + id));

        existingPenalty.setPenaltyType(penalty.getPenaltyType());
        existingPenalty.setPenaltyDetail(penalty.getPenaltyDetail());
        existingPenalty.setPenaltyDate(penalty.getPenaltyDate());
        existingPenalty.setPenaltyStatus(penalty.getPenaltyStatus());

        Penalty updatedPenalty = penaltyRepository.save(existingPenalty);

        // คุณอาจจะต้องเพิ่ม logic การอัปเดต accountStatus ที่นี่ด้วย หากมีการเปลี่ยนประเภทการลงโทษ
        // ซึ่งต้องทราบว่า Penalty นี้เกี่ยวข้องกับ Person คนใด
        // อาจจะต้อง Query หา Report ที่มี penalty_id นี้ เพื่อหา Hirer/Housekeeper
        // List<Report> reports = reportRepository.findByPenalty_PenaltyId(updatedPenalty.getPenaltyId());
        // if (!reports.isEmpty()) {
        //     Report report = reports.get(0); // สมมติว่า Penalty หนึ่งมี Report เดียว
        //     Person personToUpdate = null;
        //     if (report.getHirer() != null && report.getHirer().getPerson() != null) {
        //         personToUpdate = report.getHirer().getPerson();
        //     } else if (report.getHousekeeper() != null && report.getHousekeeper().getPerson() != null) {
        //         personToUpdate = report.getHousekeeper().getPerson();
        //     }
        //     if (personToUpdate != null) {
        //         personToUpdate.setAccountStatus(updatedPenalty.getPenaltyType());
        //         personService.savePerson(personToUpdate);
        //     }
        // }
        return updatedPenalty;
    }
}