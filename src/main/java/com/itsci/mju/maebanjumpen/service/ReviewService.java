package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.ReviewDTO;
import com.itsci.mju.maebanjumpen.model.Review; // ⚠️ ไม่ต้องใช้ Review Entity เป็น Input/Output ใน Interface
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getAllReviews();
    ReviewDTO getReviewById(int id);
    ReviewDTO saveReview(ReviewDTO reviewDto);
    void deleteReview(int id);
    ReviewDTO getReviewByHireId(int hireId);

    // ✅ แก้ไข: เปลี่ยน Review เป็น ReviewDTO
    ReviewDTO updateReview(int id, ReviewDTO reviewDto); // ⬅️ นี่คือตัวที่ถูกต้อง

    List<ReviewDTO> getReviewsByHireId(int hireId);
}