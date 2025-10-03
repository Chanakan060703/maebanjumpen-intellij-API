// src/main/java/com/itsci/mju/maebanjumpen/service/HireStatusUpdateService.java
package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.repository.HireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.TaskScheduler; // 🎯 Import TaskScheduler
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.time.Instant; // 🎯 Import Instant
import java.time.LocalDateTime;
// ลบพวก java.util.concurrent.Executors/ScheduledExecutorService/TimeUnit ออก

@Service
@RequiredArgsConstructor
public class HireStatusUpdateService {

    private final HireRepository hireRepository;
    // 🎯 Inject TaskScheduler ของ Spring
    private final TaskScheduler taskScheduler;

    // หากต้องการจัดการ Task ที่ถูกตั้งเวลาเพื่อให้ยกเลิกได้ ต้องใช้ Map
    // แต่สำหรับการแก้ไขนี้ เราจะเน้นไปที่การใช้งาน TaskScheduler เพื่อให้ @Transactional ทำงานได้
    // ถ้าไม่จำเป็นต้องยกเลิก ให้ลบ Map ออกไปได้เลย

    /**
     * เมธอดที่มี @Transactional เพื่อดำเนินการอัปเดต DB
     * จะถูกเรียกใช้โดย TaskScheduler หลังจากเวลาที่กำหนด
     * @param hireId ID ของงานจ้าง
     */
    @Transactional
    public void revertStatus(Integer hireId) {
        // ดึง Hire ล่าสุดจาก DB อีกครั้งก่อนอัปเดต
        Hire hireToUpdate = hireRepository.findById(hireId).orElse(null);

        String latestStatus = hireToUpdate != null ? hireToUpdate.getJobStatus() : null;

        // ตรวจสอบว่าควรเปลี่ยนกลับหรือไม่ (ถ้าสถานะล่าสุดเป็น Reported)
        // ถ้าสถานะเป็นอย่างอื่น (เช่น Admin เปลี่ยนเป็น Banned) จะไม่ทำการเปลี่ยนกลับ
        boolean shouldRevert = "Reported".equalsIgnoreCase(latestStatus);

        if (hireToUpdate != null && shouldRevert) {
            hireToUpdate.setJobStatus("Completed");
            hireRepository.save(hireToUpdate);
            System.out.println("✅ Hire ID " + hireId + " status reverted from '" + latestStatus + "' to 'Completed' at " + LocalDateTime.now());
        } else {
            if (hireToUpdate == null) {
                System.err.println("Hire ID " + hireId + " not found when attempting to revert status.");
            } else {
                System.out.println("⚠️ Hire ID " + hireId + " status was not eligible for revert (Current: " + latestStatus + "). No revert performed.");
            }
        }
    }


    /**
     * เมธอดสำหรับกำหนดเวลาเปลี่ยนสถานะกลับเป็น 'Completed' หลังจากเวลาที่กำหนด
     * @param hireId ID ของงานจ้าง
     * @param delayInSeconds เวลาหน่วงเป็นวินาที (long)
     */
    public void scheduleStatusRevert(Integer hireId, long delayInSeconds) {
        if (hireId == null) {
            System.err.println("Cannot schedule status revert: hireId is null.");
            return;
        }

        System.out.println("⏳ Scheduled Hire ID " + hireId + " to revert status to 'Completed' in " + delayInSeconds + " seconds.");

        // 🎯 ใช้ TaskScheduler ของ Spring เพื่อรันโค้ดภายใต้ Spring Context
        taskScheduler.schedule(() -> {
            try {
                // เรียกใช้เมธอดที่มี @Transactional เพื่ออัปเดต DB
                revertStatus(hireId);
            } catch (Exception e) {
                System.err.println("❌ Error reverting status for Hire ID " + hireId + ": " + e.getMessage());
                e.printStackTrace(); // พิมพ์ stack trace เพื่อช่วยในการ Debug
            }
        }, Instant.now().plusSeconds(delayInSeconds));
    }
}