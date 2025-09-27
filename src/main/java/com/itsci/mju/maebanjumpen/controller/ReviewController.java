package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.ReviewDTO;
import com.itsci.mju.maebanjumpen.service.ReviewService;
import lombok.RequiredArgsConstructor; // ⬅️ ใช้ Lombok แทน @Autowired สำหรับ Constructor Injection
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/maeban/reviews")
@RequiredArgsConstructor // ⬅️ ใช้ Constructor Injection
public class ReviewController {

    // ⬅️ เหลือแค่ ReviewService ที่จำเป็นเท่านั้น
    private final ReviewService reviewService;

    // ------------------------------------------------------------------
    // GET MAPPINGS (ใช้ DTO ทั้งหมด)
    // ------------------------------------------------------------------

    @GetMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable int id) {
        ReviewDTO review = reviewService.getReviewById(id);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(review);
    }

    @GetMapping(value = "/hire/{hireId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ReviewDTO> getReviewByHireId(@PathVariable int hireId) {
        // ⚠️ เนื่องจาก getReviewsByHireId คืนค่า List (แม้จะมีสมาชิกเดียว) เราควรใช้เมธอดที่คืนค่า ReviewDTO โดยตรง
        ReviewDTO review = reviewService.getReviewByHireId(hireId);

        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        // หรือถ้าต้องการให้เมธอดนี้คืนค่า List ตามโค้ดเดิม
        /*
        List<ReviewDTO> reviews = reviewService.getReviewsByHireId(hireId);
        if (reviews.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reviews.get(0)); // คืนค่าตัวแรก
        */
        return ResponseEntity.ok(review);
    }

    // ------------------------------------------------------------------
    // POST MAPPING (ใช้ DTO และย้ายตรรกะไป Service)
    // ------------------------------------------------------------------

    @PostMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDto) { // ⬅️ รับ ReviewDTO
        try {
            // ⬅️ เรียกใช้ Service เท่านั้น ตรรกะการตรวจสอบทั้งหมดอยู่ใน Service
            ReviewDTO savedReview = reviewService.saveReview(reviewDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
        } catch (IllegalArgumentException e) {
            // กรณี Hire ID หายไป
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalStateException e) {
            // กรณีมีการ Review ซ้ำ (Conflict)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            // กรณี Hire Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating review", e);
        }
    }

    // ------------------------------------------------------------------
    // PUT MAPPING (ใช้ DTO และย้ายตรรกะไป Service)
    // ------------------------------------------------------------------

    @PutMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable int id, @RequestBody ReviewDTO reviewDto) { // ⬅️ รับ ReviewDTO
        try {
            // ⬅️ เรียกใช้ Service เท่านั้น ตรรกะการอัปเดตและคำนวณเรตติ้งอยู่ใน Service
            ReviewDTO updatedReview = reviewService.updateReview(id, reviewDto);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            // เช่น Review Not Found หรือ Hire Not Found
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating review", e);
        }
    }

    // ------------------------------------------------------------------
    // DELETE MAPPING (ใช้ DTO และย้ายตรรกะไป Service)
    // ------------------------------------------------------------------

    @DeleteMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Void> deleteReview(@PathVariable int id) {
        // ⬅️ ตรรกะการตรวจสอบและคำนวณเรตติ้งหลังลบ ถูกย้ายไปอยู่ใน Service
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}