package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.model.Review;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.repository.ReviewRepository;
import com.itsci.mju.maebanjumpen.service.ReviewService;
import com.itsci.mju.maebanjumpen.repository.HireRepository;
import com.itsci.mju.maebanjumpen.service.HireService; // Import HireService
import com.itsci.mju.maebanjumpen.service.HousekeeperService; // Import HousekeeperService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/maeban/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private HireRepository hireRepository;

    @Autowired // เพิ่ม autowire สำหรับ HireService
    private HireService hireService;

    @Autowired // เพิ่ม autowire สำหรับ HousekeeperService
    private HousekeeperService housekeeperService;


    // แก้ไข: เพิ่ม produces = "application/json;charset=UTF-8" เพื่อรองรับภาษาไทย
    @GetMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    // แก้ไข: เพิ่ม produces = "application/json;charset=UTF-8" เพื่อรองรับภาษาไทย
    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Review> getReviewById(@PathVariable int id) {
        Review review = reviewService.getReviewById(id);
        if (review == null) { // เพิ่มการตรวจสอบ null
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(review);
    }

    // แก้ไข: เพิ่ม produces = "application/json;charset=UTF-8" เพื่อรองรับภาษาไทย
    @PostMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        // 1. ตรวจสอบว่า Review ที่เข้ามามี Hire ID หรือไม่
        if (review.getHire() == null || review.getHire().getHireId() == null) {
            System.err.println("Error: Review object must contain a valid Hire ID.");
            return ResponseEntity.badRequest().body(null); // HTTP 400 Bad Request
        }

        Integer hireId = review.getHire().getHireId();

        // 2. ดึง Hire object ที่สมบูรณ์จากฐานข้อมูล
        Optional<Hire> existingHireOptional = hireRepository.findById(hireId);
        if (existingHireOptional.isEmpty()) {
            System.err.println("Error: Hire with ID " + hireId + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // HTTP 404 Not Found
        }
        Hire existingHire = existingHireOptional.get();

        // 3. ตรวจสอบว่า Hire นี้มี Review อยู่แล้วหรือไม่
        // เนื่องจากความสัมพันธ์เป็น @OneToOne และ hire_id เป็น unique constraint
        // เราต้องแน่ใจว่าไม่มี Review สำหรับ Hire นี้อยู่แล้ว
        if (existingHire.getReview() != null) {
            System.err.println("Error: Hire with ID " + hireId + " already has a review (Review ID: " + existingHire.getReview().getReviewId() + ").");
            // ถ้ามีรีวิวอยู่แล้ว ส่ง HTTP 409 Conflict กลับไป
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(existingHire.getReview()); // อาจจะส่ง review เดิมกลับไป หรือ null ก็ได้
        }

        // 4. ตั้งค่า Hire object ที่สมบูรณ์ให้กับ Review
        review.setHire(existingHire);
        // กำหนดวันที่รีวิวอัตโนมัติหากยังไม่ได้ตั้งค่า
        if (review.getReviewDate() == null) {
            review.setReviewDate(LocalDateTime.now());
        }

        // 5. บันทึก Review ใหม่
        Review savedReview = reviewRepository.save(review);
        System.out.println("Review created successfully for Hire ID: " + savedReview.getHire().getHireId());

        // 6. อัปเดตเรตติ้งของ Housekeeper ที่เกี่ยวข้อง
        if (savedReview.getHire() != null && savedReview.getHire().getHousekeeper() != null) {
            Integer housekeeperId = savedReview.getHire().getHousekeeper().getId();
            housekeeperService.calculateAndSetAverageRating(housekeeperId);
            System.out.println("Triggered average rating update for Housekeeper ID: " + housekeeperId);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview); // HTTP 201 Created
    }

    @PutMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Review> updateReview(@PathVariable int id, @RequestBody Review review) {
        // ... โค้ดเดิม
        Review updatedReview = reviewService.updateReview(id, review);
        if (updatedReview == null) {
            return ResponseEntity.notFound().build();
        }

        if (updatedReview.getHire() != null && updatedReview.getHire().getHousekeeper() != null && updatedReview.getHire().getHousekeeper().getId() != null) {
            housekeeperService.calculateAndSetAverageRating(updatedReview.getHire().getHousekeeper().getId()); // <<< ตรงนี้!
            System.out.println("Triggered average rating update after review update for Housekeeper ID: " + updatedReview.getHire().getHousekeeper().getId());
        }
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Void> deleteReview(@PathVariable int id) {
        Review reviewToDelete = reviewService.getReviewById(id);
        if (reviewToDelete != null && reviewToDelete.getHire() != null && reviewToDelete.getHire().getHousekeeper() != null) {
            int housekeeperId = reviewToDelete.getHire().getHousekeeper().getId();
            reviewService.deleteReview(id);
            housekeeperService.calculateAndSetAverageRating(housekeeperId); // <<< ตรงนี้!
            System.out.println("Triggered average rating update after review deletion for Housekeeper ID: " + housekeeperId);
        } else {
            reviewService.deleteReview(id);
            System.err.println("Warning: Could not find associated Hire or Housekeeper for review ID: " + id + " to update rating after deletion.");
        }
        return ResponseEntity.noContent().build();
    }

    // *** เมธอดใหม่สำหรับดึงรีวิวตาม Hire ID ***
    @GetMapping(value = "/hire/{hireId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Review>> getReviewsByHireId(@PathVariable int hireId) {
        List<Review> reviews = reviewService.getReviewsByHireId(hireId);
        if (reviews.isEmpty()) { // ตรวจสอบว่า list ว่างเปล่าหรือไม่
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reviews);
    }
}
