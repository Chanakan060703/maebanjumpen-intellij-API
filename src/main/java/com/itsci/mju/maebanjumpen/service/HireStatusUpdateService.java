// src/main/java/com/itsci/mju/maebanjumpen/service/HireStatusUpdateService.java
package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.repository.HireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class HireStatusUpdateService {

    @Autowired
    private HireRepository hireRepository;

    private final Map<Integer, ScheduledExecutorService> scheduledTasks = new ConcurrentHashMap<>();

    // เมธอดสำหรับกำหนดเวลาเปลี่ยนสถานะกลับ
    public void scheduleStatusRevert(Integer hireId, long delayInSeconds) {
        if (hireId == null) {
            System.err.println("Cannot schedule status revert: hireId is null.");
            return;
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduledTasks.put(hireId, scheduler);

        scheduler.schedule(() -> {
            try {
                // ดึง Hire ล่าสุดจาก DB อีกครั้งก่อนอัปเดต
                Hire hireToUpdate = hireRepository.findById(hireId).orElse(null);
                if (hireToUpdate != null && "Reviewed".equalsIgnoreCase(hireToUpdate.getJobStatus())) {
                    hireToUpdate.setJobStatus("Completed");
                    hireRepository.save(hireToUpdate);
                    System.out.println("Hire ID " + hireId + " status reverted to 'Completed' at " + LocalDateTime.now());
                } else {
                    if (hireToUpdate == null) {
                        System.err.println("Hire ID " + hireId + " not found when attempting to revert status.");
                    } else {
                        System.out.println("Hire ID " + hireId + " status was not 'Reviewed'. Current status: " + hireToUpdate.getJobStatus() + ". No revert performed.");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reverting status for Hire ID " + hireId + ": " + e.getMessage());
            } finally {
                // ปิด scheduler หลังจากงานเสร็จสิ้น
                scheduler.shutdown();
                scheduledTasks.remove(hireId);
            }
        }, delayInSeconds, TimeUnit.SECONDS);

        System.out.println("Scheduled Hire ID " + hireId + " to revert status to 'Completed' in " + delayInSeconds + " seconds.");
    }
}