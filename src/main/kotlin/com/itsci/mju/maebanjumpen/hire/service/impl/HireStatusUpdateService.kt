package com.itsci.mju.maebanjumpen.hire.service.impl

import com.itsci.mju.maebanjumpen.hire.repository.HireRepository
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime

@Service
class HireStatusUpdateService(
    private val hireRepository: HireRepository,
    private val taskScheduler: TaskScheduler
) {

    @Transactional
    fun revertStatus(hireId: Int) {
        val hireToUpdate = hireRepository.findById(hireId).orElse(null)

        val latestStatus = hireToUpdate?.jobStatus

        val shouldRevert = "Reported".equals(latestStatus, ignoreCase = true)

        if (hireToUpdate != null && shouldRevert) {
            hireToUpdate.jobStatus = "Completed"
            hireRepository.save(hireToUpdate)
            println("✅ Hire ID $hireId status reverted from '$latestStatus' to 'Completed' at ${LocalDateTime.now()}")
        } else {
            if (hireToUpdate == null) {
                System.err.println("Hire ID $hireId not found when attempting to revert status.")
            } else {
                println("⚠️ Hire ID $hireId status was not eligible for revert (Current: $latestStatus). No revert performed.")
            }
        }
    }

    fun scheduleStatusRevert(hireId: Int, delayInSeconds: Long) {
        println("⏳ Scheduled Hire ID $hireId to revert status to 'Completed' in $delayInSeconds seconds.")

        taskScheduler.schedule({
            try {
                revertStatus(hireId)
            } catch (e: Exception) {
                System.err.println("❌ Error reverting status for Hire ID $hireId: ${e.message}")
                e.printStackTrace()
            }
        }, Instant.now().plusSeconds(delayInSeconds))
    }
}

