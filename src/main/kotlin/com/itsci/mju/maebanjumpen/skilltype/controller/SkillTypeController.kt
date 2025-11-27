package com.itsci.mju.maebanjumpen.skilltype.controller

import com.itsci.mju.maebanjumpen.skilltype.dto.SkillTypeDTO
import com.itsci.mju.maebanjumpen.skilltype.request.CreateSkillTypeRequest
import com.itsci.mju.maebanjumpen.skilltype.request.UpdateSkillTypeRequest
import com.itsci.mju.maebanjumpen.skilltype.service.SkillTypeService
import com.luca.intern.common.exception.BadRequestException
import com.luca.intern.common.exception.NotFoundException
import com.luca.intern.common.response.HttpResponse
import org.springframework.http.HttpStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/skill-types")
class SkillTypeController @Autowired internal constructor(
  private val skillTypeService: SkillTypeService
) {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  @GetMapping
  fun listAllSkillTypes(

  ): ResponseEntity<Any> {
   return try {
     ResponseEntity.ok(
       HttpResponse(
         true,
         "ดึงรายการ skill type สำเร็จ",
         skillTypeService.listAllSkillTypes()
       )
     )
   } catch (e: BadRequestException) {
     logger.error(e.message)
     ResponseEntity.badRequest().body(
       HttpResponse(
         false,
         e.message ?: "รายการ product คำร้องขอไม่ถูกต้อง"
       )
     )
   } catch (e: NotFoundException) {
     logger.error(e.message)
     ResponseEntity.badRequest().body(
       HttpResponse(
         false,
         e.message ?: "รายการ product ไม่พบข้อมูล"
       )
     )
   } catch (e: Exception) {
     logger.error(e.message)
     ResponseEntity.badRequest().body(
       HttpResponse(
         false,
         e.message ?: "ดึงรายการ product ไม่สำเร็จ"
       )
     )
   }
  }

  @GetMapping("/{id}")
  fun getSkillTypeById(
    @PathVariable id: Long
  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "ดึงรายการ skill type สำเร็จ",
          skillTypeService.getSkillTypeById(id)
        )
      )
    } catch (e: NotFoundException) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ product ไม่พบข้อมูล"
        )
      )
    } catch (e: Exception) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ product ไม่สำเร็จ"
        )
      )
    }
  }
  @PostMapping
  fun createSkillType(
    @RequestBody request: CreateSkillTypeRequest
  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "เพิ่มรายการ skill type สำเร็จ",
          skillTypeService.createSkillType(request)
        )
      )
    } catch (e: BadRequestException) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ product คำร้องขอไม่ถูกต้อง"
        )
      )
    } catch (e: Exception) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "เพิ่มรายการ product ไม่สำเร็จ"
        )
      )
    }
  }

  @PutMapping("/{id}")
  fun updateSkillType(
    @PathVariable id: Long,
    @RequestBody request: UpdateSkillTypeRequest
  ): ResponseEntity<Any> {
    return try {
      request.id = id
      val skillType = skillTypeService.updateSkillType(request)
      ResponseEntity.ok(
        HttpResponse(
          true,
          "อัพเดทรายการ skill type สำเร็จ",
          skillType
        )
      )
    } catch (e: NotFoundException) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "อัพเดทรายการ product ไม่พบข้อมูล"
        )
      )
    } catch (e: Exception) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "อัพเดทรายการ product ไม่สำเร็จ"
        )
      )
    }
  }

  @DeleteMapping("/{id}")
  fun deleteSkillType(
    @PathVariable id: Long
  ): ResponseEntity<Any> {
    return try {
      skillTypeService.deleteSkillType(id)
      ResponseEntity.ok(
        HttpResponse(
          true,
          "ลบรายการ skill type สำเร็จ"
        )
      )
    } catch (e: Exception) {
      logger.error(e.message)
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "ลบรายการ product ไม่สำเร็จ"
        )
      )
    }
  }


}

