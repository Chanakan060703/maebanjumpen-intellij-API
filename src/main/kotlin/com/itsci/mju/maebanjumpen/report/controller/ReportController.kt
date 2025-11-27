package com.itsci.mju.maebanjumpen.report.controller

import com.itsci.mju.maebanjumpen.report.dto.ReportDTO
import com.itsci.mju.maebanjumpen.report.request.CreateReportRequest
import com.itsci.mju.maebanjumpen.report.request.UpdateReportRequest
import com.itsci.mju.maebanjumpen.report.service.ReportService
import com.luca.intern.common.exception.BadRequestException
import com.luca.intern.common.exception.NotFoundException
import com.luca.intern.common.response.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/reports")
class ReportController @Autowired internal constructor(
  private val reportService: ReportService
) {

    @GetMapping
    fun listAllReports(

    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(
              HttpResponse(
                true,
                "รายการ report สำเร็จ",
                reportService.listAllReports()
              )
            )
        } catch (e: BadRequestException){
            ResponseEntity.badRequest().body(
              HttpResponse(
                false,
                e.message ?: "รายการ report คำร้องขอไม่ถูกต้อง"
              )
            )
        } catch (e: NotFoundException) {
            ResponseEntity.badRequest().body(
              HttpResponse(
                false,
                e.message ?: "รายการ report ไม่พบข้อมูล"
              )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
              HttpResponse(
                false,
                e.message ?: "รายการ report ไม่สำเร็จ"
              )
            )
        }
    }

//    @GetMapping("/by-hire/{hireId}")
//    fun getReportsByHireId(@PathVariable hireId: Int): ResponseEntity<Any> {
//        return try {
//            ResponseEntity.ok(
//              HttpResponse(
//                true,
//                "รายการ report สำเร็จ",
//                reportService.findByHire_HireId(hireId.toLong())
//              )
//            )
//        } catch (e: BadRequestException) {
//            ResponseEntity.badRequest().body(
//              HttpResponse(
//                false,
//                e.message ?: "รายการ report คำร้องขอไม่ถูกต้อง"
//              )
//            )
//        } catch (e: NotFoundException) {
//            ResponseEntity.badRequest().body(
//              HttpResponse(
//                false,
//                e.message ?: "รายการ report ไม่พบข้อมูล"
//              )
//            )
//        } catch (e: Exception) {
//            ResponseEntity.badRequest().body(
//              HttpResponse(
//                false,
//                e.message ?: "รายการ report ไม่สำเร็จ"
//              )
//            )
//        }
//    }

//    @GetMapping("/by-hire/{hireId}/by-reporter/{reporterId}")
//    fun getReportByHireIdAndReporterId(
//      @PathVariable hireId: Int,
//      @PathVariable reporterId: Long
//    ): ResponseEntity<Any> {
//        return try {
//            ResponseEntity.ok(
//              HttpResponse(
//                true,
//                "รายการ report สำเร็จ",
//                reportService.findByHire_HireIdAndReporter_Id(hireId, reporterId)
//              )
//            )
//        } catch (e: BadRequestException) {
//            ResponseEntity.badRequest().body(
//              HttpResponse(
//                false,
//                e.message ?: "รายการ report คำร้องขอไม่ต้อง"
//              )
//            )
//        } catch (e: NotFoundException) {
//            ResponseEntity.badRequest().body(
//              HttpResponse(
//                false,
//                e.message ?: "รายการ report ไม่พบข้อมูล"
//              )
//            )
//        } catch (e: Exception) {
//            ResponseEntity.badRequest().body(
//              HttpResponse(
//                false,
//                e.message ?: "รายการ report ไม่สำเร็จ"
//              )
//            )
//        }
//    }

    @PostMapping
    fun createReport(
      @RequestBody request: CreateReportRequest
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(
              HttpResponse(
                true,
                "รายการ report สำเร็จ",
                reportService.createReport(request)
              )
            )
        } catch (e: BadRequestException) {
            ResponseEntity.badRequest().body(
              HttpResponse(
                false,
                e.message ?: "รายการ report คำร้องขอไม่ต้อง"
              )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
              HttpResponse(
                false,
                e.message ?: "รายการ report ไม่สำเร็จ"
              )
            )
        }
    }

  @PutMapping("/{id}")
  fun updateReport(
    @PathVariable id: Long,
    @RequestBody request: UpdateReportRequest
  ): ResponseEntity<Any> {
    return try {
      request.id = id
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ report สำเร็จ",
          reportService.updateReport(request)
        )
      )
    } catch (e: NotFoundException) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ report ไม่พบข้อมูล"
        )
      )
    } catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ report ไม่สำเร็จ"
        )
      )
    }
  }
  @DeleteMapping("/{id}")
  fun deleteReport(
    @PathVariable id: Long
  ): ResponseEntity<Any> {
    return try {
      reportService.deleteReport(id)
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ report สำเร็จ"
        )
      )
    } catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ report ไม่สำเร็จ"
        )
      )
    }
  }
  @PutMapping("/ban/{personId}")
  fun updateUserAccountStatus(
    @PathVariable personId: Long,
    @RequestParam isBanned: Boolean
  ): ResponseEntity<Any> {
    return try {
      reportService.updateUserAccountStatus(personId, isBanned)
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ report สำเร็จ"
        )
      )
    } catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ report ไม่สำเร็จ"
        )
      )
    }
  }


}

