package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Review;

import java.util.List;

public interface ReviewService {
    List<Review> getAllReviews();
    Review getReviewById(int id);
    Review saveReview(Review review);
    void deleteReview(int id);
    Review getReviewByHireId(int hireId); // เมธอดนี้อัปเดตเป็น findByHire_HireId แล้วใน ServiceImpl

    Review updateReview(int id, Review review);

    List<Review> getReviewsByHireId(int hireId);
}