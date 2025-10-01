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

    private final ReviewService reviewService;

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
        return ResponseEntity.ok(review);
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDto) { // ⬅️ รับ ReviewDTO
        try {
            ReviewDTO savedReview = reviewService.saveReview(reviewDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating review", e);
        }
    }
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

    @DeleteMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Void> deleteReview(@PathVariable int id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}