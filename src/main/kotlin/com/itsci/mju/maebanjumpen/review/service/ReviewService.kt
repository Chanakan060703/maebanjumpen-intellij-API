package com.itsci.mju.maebanjumpen.review.service

import com.itsci.mju.maebanjumpen.review.dto.ReviewDTO
import com.itsci.mju.maebanjumpen.review.request.CreateReviewRequest
import com.itsci.mju.maebanjumpen.review.request.UpdateReviewRequest

interface ReviewService {
    fun listAllReviews(): List<ReviewDTO>
    fun getReviewById(id: Long): ReviewDTO?

    fun createReview(request: CreateReviewRequest): ReviewDTO

    fun deleteReview(id: Long)

    fun getReviewByHireId(hireId: Long): ReviewDTO?

    fun updateReview(request: UpdateReviewRequest): ReviewDTO

    fun getReviewsByHireId(hireId: Long): List<ReviewDTO>

    fun getReviewsByHousekeeperId(housekeeperId: Long): List<ReviewDTO>
}

