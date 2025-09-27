package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.ReviewDTO;
import com.itsci.mju.maebanjumpen.mapper.ReviewMapper;
import com.itsci.mju.maebanjumpen.model.Review;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.repository.ReviewRepository;
import com.itsci.mju.maebanjumpen.repository.HireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final HireRepository hireRepository;
    private final HousekeeperService housekeeperService; // 💡 ถูก Inject แล้ว
    private final HireStatusUpdateService hireStatusUpdateService;
    private final ReviewMapper reviewMapper;

    @Override
    public List<ReviewDTO> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll(); // ⬅️ เรียกใช้เมธอดที่มี @EntityGraph
        return reviewMapper.toDtoList(reviews);
    }

    @Override
    public ReviewDTO getReviewById(int id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toDto)
                .orElse(null);
    }

    @Override
    public ReviewDTO getReviewByHireId(int hireId) {
        Review review = reviewRepository.findByHire_HireId(hireId);
        return review != null ? reviewMapper.toDto(review) : null;
    }

    @Override
    @Transactional
    public ReviewDTO saveReview(ReviewDTO reviewDto) {

        Integer hireId = reviewDto.getHireId();
        if (hireId == null) {
            throw new IllegalArgumentException("Hire ID is required for saving a review.");
        }

        Optional<Hire> existingHireOptional = hireRepository.findById(hireId);

        if (existingHireOptional.isEmpty()) {
            throw new RuntimeException("Hire with ID " + hireId + " not found.");
        }

        Hire existingHire = existingHireOptional.get();

        if (existingHire.getReview() != null) {
            throw new IllegalStateException("Hire with ID " + hireId + " already has a review (Review ID: " + existingHire.getReview().getReviewId() + ").");
        }

        Review review = reviewMapper.toEntity(reviewDto);
        review.setHire(existingHire);

        Review savedReview = reviewRepository.save(review);

        // Update Hire Status
        existingHire.setJobStatus("Reviewed");
        hireRepository.save(existingHire);

        // Schedule status revert (ตรรกะการเปลี่ยนสถานะกลับ)
        if (existingHire.getHireId() != null) {
            // ⚠️ สมมติว่าต้องการให้สถานะกลับเป็น "Completed"
            // เนื่องจากโค้ดก่อนหน้าใช้ 3 วินาที ผมจึงคงตัวเลขนี้ไว้
            hireStatusUpdateService.scheduleStatusRevert(existingHire.getHireId(), 3);
        }

        // ✅ Update Housekeeper Rating: การเรียกใช้ที่ถูกต้อง
        if (savedReview.getHire().getHousekeeper() != null) {
            Integer housekeeperId = savedReview.getHire().getHousekeeper().getId();
            housekeeperService.calculateAndSetAverageRating(housekeeperId);
        }

        return reviewMapper.toDto(savedReview);
    }

    @Override
    @Transactional
    public void deleteReview(int id) {
        Optional<Review> reviewToDeleteOptional = reviewRepository.findById(id);

        if (reviewToDeleteOptional.isPresent()) {
            Review reviewToDelete = reviewToDeleteOptional.get();
            reviewRepository.deleteById(id);

            // ✅ Recalculate rating after deletion
            if (reviewToDelete.getHire() != null && reviewToDelete.getHire().getHousekeeper() != null) {
                Integer housekeeperId = reviewToDelete.getHire().getHousekeeper().getId();
                housekeeperService.calculateAndSetAverageRating(housekeeperId);
            }
        }
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(int id, ReviewDTO reviewDto) { // ⬅️ เมธอดที่ได้รับการแก้ไข
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review with ID " + id + " not found"));

        // 1. อัปเดต fields พื้นฐาน
        // 💡 ตรวจสอบว่ามีการเปลี่ยนแปลงคะแนนหรือไม่ก่อน
        Double oldScore = existingReview.getScore();
        Double newScore = reviewDto.getScore() != null ? Double.valueOf(reviewDto.getScore()) : oldScore;

        if (reviewDto.getReviewMessage() != null) existingReview.setReviewMessage(reviewDto.getReviewMessage());
        if (reviewDto.getReviewDate() != null) existingReview.setReviewDate(reviewDto.getReviewDate());
        if (reviewDto.getScore() != null) existingReview.setScore(newScore);


        // 2. การจัดการ Hire ID (ผูก Hire ใหม่ ถ้ามีการเปลี่ยนแปลง)
        if (reviewDto.getHireId() != null) {
            if (existingReview.getHire() == null || !existingReview.getHire().getHireId().equals(reviewDto.getHireId())) {
                // Fetch และผูก Hire Entity ใหม่
                Hire newHire = hireRepository.findById(reviewDto.getHireId())
                        .orElseThrow(() -> new RuntimeException("Hire with ID " + reviewDto.getHireId() + " not found"));
                existingReview.setHire(newHire);
            }
        } else if (reviewDto.getHireId() == null) {
            existingReview.setHire(null);
        }

        // 3. Save Review
        Review updatedReview = reviewRepository.save(existingReview);

        // 4. ✅ Recalculate Housekeeper Rating: แม้จะไม่มีการเปลี่ยนคะแนน ก็ควรคำนวณใหม่เพื่อความปลอดภัย
        if (updatedReview.getHire() != null && updatedReview.getHire().getHousekeeper() != null) {
            Integer housekeeperId = updatedReview.getHire().getHousekeeper().getId();
            housekeeperService.calculateAndSetAverageRating(housekeeperId);
        }

        // 5. แปลง Entity กลับเป็น DTO และคืนค่า
        return reviewMapper.toDto(updatedReview);
    }

    @Override
    public List<ReviewDTO> getReviewsByHireId(int hireId) {
        // เนื่องจาก Review เป็น OneToOne กับ Hire เมธอดนี้จึงคืนค่า List ที่มีสมาชิกเดียว
        Review review = reviewRepository.findByHire_HireId(hireId);
        if (review != null) {
            return List.of(reviewMapper.toDto(review));
        }
        return List.of();
    }
}