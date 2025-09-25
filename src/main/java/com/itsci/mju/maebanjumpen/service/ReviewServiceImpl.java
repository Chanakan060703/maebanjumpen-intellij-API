package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Review;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.repository.ReviewRepository;
import com.itsci.mju.maebanjumpen.repository.HireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private HireRepository hireRepository;

    @Autowired
    private HousekeeperService housekeeperService;

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
        // Check if the review object contains a valid hire ID.
        if (review.getHire() == null || review.getHire().getHireId() == null) {
            System.err.println("Error: Review object must contain a valid Hire ID.");
            return null;
        }

        // 1. Fetch the Hire object from the database to ensure it's a managed entity.
        Optional<Hire> existingHireOptional = hireRepository.findById(review.getHire().getHireId());

        if (existingHireOptional.isEmpty()) {
            System.err.println("Error: Hire with ID " + review.getHire().getHireId() + " not found.");
            return null;
        }

        Hire existingHire = existingHireOptional.get();

        // Check if a review for this hire already exists.
        if (existingHire.getReview() != null) {
            System.err.println("Error: Hire with ID " + existingHire.getHireId() + " already has a review (Review ID: " + existingHire.getReview().getReviewId() + ").");
            return null;
        }

        // Set the managed hire entity to the review object.
        review.setHire(existingHire);

        // 2. Save the new review to the database.
        Review savedReview = reviewRepository.save(review);
        System.out.println("Review created successfully for Hire ID: " + savedReview.getHire().getHireId());

        // 3. Update the job status of the associated hire to "Reviewed".
        existingHire.setJobStatus("Reviewed");
        hireRepository.save(existingHire);
        System.out.println("Updated Hire status to 'Reviewed' for ID: " + existingHire.getHireId());

        // 4. After successfully saving, get the housekeeper's ID from the review's hire.
        // The rating should be updated only if the review is for a housekeeper.
        if (savedReview.getHire() != null && savedReview.getHire().getHousekeeper() != null) {
            Integer housekeeperId = savedReview.getHire().getHousekeeper().getId();

            // 5. Call the HousekeeperService to recalculate the rating.
            // This ensures the housekeeper's rating is always up-to-date.
            housekeeperService.calculateAndSetAverageRating(housekeeperId);
            System.out.println("Triggered average rating update for Housekeeper ID: " + housekeeperId);
        }

        // Re-fetch the saved review with all its details before returning.
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
            // Fetch the hire with all details to ensure it's a managed entity.
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
