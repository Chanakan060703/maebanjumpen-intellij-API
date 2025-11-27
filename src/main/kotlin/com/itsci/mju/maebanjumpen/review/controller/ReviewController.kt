package com.itsci.mju.maebanjumpen.review.controller

import com.itsci.mju.maebanjumpen.review.dto.ReviewDTO
import com.itsci.mju.maebanjumpen.review.request.CreateReviewRequest
import com.itsci.mju.maebanjumpen.review.request.UpdateReviewRequest
import com.itsci.mju.maebanjumpen.review.service.ReviewService
import com.luca.intern.common.exception.BadRequestException
import com.luca.intern.common.exception.NotFoundException
import com.luca.intern.common.response.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/review")
class ReviewController @Autowired internal constructor(
  private val reviewService: ReviewService
) {

  @GetMapping
  fun listAllReviews(

  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ review สำเร็จ",
          reviewService.listAllReviews()
        )
      )
    } catch(e: BadRequestException) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการคำขอ review ไม่ถูกต้อง"
        )
      )
    } catch (e: NotFoundException) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่พบข้อมูล"
        )
      )
    }
    catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่สำเร็จ"
        )
      )
    }
  }

  @GetMapping("/{id}")
  fun getReviewById(
    @PathVariable id: Long
  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ review สำเร็จ",
          reviewService.getReviewById(id)
        )
      )
    } catch (e: NotFoundException) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่พบข้อมูล"
        )
      )
    } catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่สำเร็จ"
        )
      )
    }
  }

  @PostMapping
  fun createReview(
    @RequestBody request: CreateReviewRequest
  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ review สำเร็จ",
          reviewService.createReview(request)
        )
      )
    } catch (e: BadRequestException) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่ต้อง"
        )
      )
    } catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่สำเร็จ"
        )
      )
    }
  }

  @DeleteMapping("/{id}")
  fun deleteReview(
    @PathVariable id: Long
  ): ResponseEntity<Any> {
    return try {
      reviewService.deleteReview(id)
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ review สำเร็จ"
        )
      )
    } catch (e: NotFoundException) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่พบข้อมูล"
        )
      )
    } catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่สำเร็จ"
        )
      )
    }
  }

  @PutMapping("/{id}")
  fun updateReview(
    @PathVariable id: Long,
    @RequestBody request: UpdateReviewRequest
  ): ResponseEntity<Any> {
    return try {
      request.id = id
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ review สำเร็จ",
          reviewService.updateReview(request)
        )
      )
    } catch (e: NotFoundException) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่พบข้อมูล"
        )
      )
    } catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่สำเร็จ"
        )
      )
    }
  }

  @GetMapping("/hire/{hireId}")
  fun getReviewByHireId(
    @PathVariable hireId: Long
  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ review สำเร็จ",
          reviewService.getReviewByHireId(hireId)
        )
      )
    } catch (e: NotFoundException) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่พบข้อมูล"
        )
      )
    } catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่สำเร็จ"
        )
      )
    }
  }

  @GetMapping("/hires/{hireId}")
  fun getReviewsByHireId(
    @PathVariable hireId: Long
  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ review สำเร็จ",
          reviewService.getReviewsByHireId(hireId)
        )
      )
    } catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่สำเร็จ"
        )
      )
    }
  }

  @GetMapping("/housekeeper/{housekeeperId}")
  fun getReviewsByHousekeeperId(
    @PathVariable housekeeperId: Long
  ): ResponseEntity<Any> {
    return try {
      ResponseEntity.ok(
        HttpResponse(
          true,
          "รายการ review สำเร็จ",
          reviewService.getReviewsByHousekeeperId(housekeeperId)
        )
      )
    } catch (e: Exception) {
      ResponseEntity.badRequest().body(
        HttpResponse(
          false,
          e.message ?: "รายการ review ไม่สำเร็จ"
        )
      )
    }
  }


}

