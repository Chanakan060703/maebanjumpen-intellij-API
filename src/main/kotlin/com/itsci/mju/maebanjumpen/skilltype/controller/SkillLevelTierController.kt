package com.itsci.mju.maebanjumpen.skilltype.controller

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillLevelTierDTO
import com.itsci.mju.maebanjumpen.skilltype.request.CreateSkillTierRequest
import com.itsci.mju.maebanjumpen.skilltype.request.UpdateSkillTierRequest
import com.itsci.mju.maebanjumpen.skilltype.service.SkillLevelTierService
import com.luca.intern.common.response.HttpResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/skill-level-tier")
class SkillLevelTierController @Autowired internal constructor(
  private val skillLevelTierService: SkillLevelTierService
) {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  @GetMapping
  fun listAllSkillLevelTiers(): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ skill level tier สำเร็จ",
          skillLevelTierService.listAllSkillLevelTiers()
        )
      )
    } catch (e: Exception) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ skill level tier ไม่สำเร็จ"
        )
      )
    }
  }

  @GetMapping("/{id}")
  fun getSkillLevelTierById(
    @PathVariable id: Long
  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ skill level tier สำเร็จ",
          skillLevelTierService.getSkillLevelTierById(id)
        )
      )
    } catch (e: Exception) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ skill level tier ไม่สำเร็จ"
        )
      )
    }
  }

  @PostMapping
  fun createSkillLevelTier(
    @RequestBody request: CreateSkillTierRequest
  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "สร้างรายการ skill level tier สำเร็จ",
          skillLevelTierService.createSkillLevelTier(request)
        )
      )
    } catch (e: Exception) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "สร้างรายการ skill level tier ไม่สำเร็จ"
        )
      )
    }
  }

  @PutMapping("/{id}")
  fun updateSkillLevelTier(
    @RequestBody request: UpdateSkillTierRequest
  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "อัพเดทรายการ skill level tier สำเร็จ",
          skillLevelTierService.updateSkillLevelTier(request)
        )
      )
    }
    catch (e: Exception) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "เดทรายการ skill level tier ไม่สำเร็จ"
        )
      )
    }
  }

  @DeleteMapping("/{id}")
  fun deleteSkillLevelTier(
    @PathVariable id: Long
  ): ResponseEntity<Any> {
    return try {
      skillLevelTierService.deleteSkillLevelTier(id)
      ResponseEntity.ok(
        HttpResponse(
          true,
          "ลบรายการ skill level tier สำเร็จ"
        )
      )
    } catch (e: Exception) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "ลบรายการ skill level tier ไม่สำเร็จ"
        )
      )
    }
  }
}

