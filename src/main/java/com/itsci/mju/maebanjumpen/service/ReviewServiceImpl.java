// src/main/java/com/itsci/mju/maebanjumpen/service/ReviewServiceImpl.java
package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Review;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.repository.ReviewRepository;
import com.itsci.mju.maebanjumpen.repository.HireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private HireRepository hireRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Review getReviewById(int id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Review saveReview(Review review) {
        if (review.getHire() != null && review.getHire().getHireId() != null) {
            // *** แก้ไขตรงนี้ให้เรียกเมธอด fetchByIdWithAllDetails ***
            Hire managedHire = hireRepository.fetchByIdWithAllDetails(review.getHire().getHireId())
                    .orElseThrow(() -> new RuntimeException("Hire with ID " + review.getHire().getHireId() + " not found"));
            review.setHire(managedHire);
        }

        Review savedReview = reviewRepository.save(review);
        // Re-fetch the saved review with all its details
        return reviewRepository.findById(savedReview.getReviewId()).orElse(null);
    }

    @Override
    @Transactional
    public void deleteReview(int id) {
        reviewRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Review getReviewByHireId(int hireId) {
        return reviewRepository.findByHire_HireId(hireId);
    }

    @Override
    @Transactional
    public Review updateReview(int id, Review review) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review with ID " + id + " not found"));

        existingReview.setReviewMessage(review.getReviewMessage());
        existingReview.setReviewDate(review.getReviewDate());
        existingReview.setScore(review.getScore());

        if (review.getHire() != null && review.getHire().getHireId() != null) {
            // *** แก้ไขตรงนี้ให้เรียกเมธอด fetchByIdWithAllDetails ***
            Hire managedHire = hireRepository.fetchByIdWithAllDetails(review.getHire().getHireId())
                    .orElseThrow(() -> new RuntimeException("Hire with ID " + review.getHire().getHireId() + " not found"));
            existingReview.setHire(managedHire);
        } else {
            existingReview.setHire(null);
        }

        Review updatedReview = reviewRepository.save(existingReview);
        // Re-fetch the updated review with all its details
        return reviewRepository.findById(updatedReview.getReviewId()).orElse(null);
    }

    @Override
    public List<Review> getReviewsByHireId(int hireId) {
        return List.of();
    }
}