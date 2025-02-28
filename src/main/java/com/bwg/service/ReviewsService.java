package com.bwg.service;

import com.bwg.domain.Reviews;
import com.bwg.model.ReviewsModel;

import java.util.List;

public interface ReviewsService {
    List<Reviews> getAllReviews();

    Reviews getReviewById(Long reviewId);

    Reviews createReview(ReviewsModel reviewsModel);

    Reviews updateReview(Long reviewId, ReviewsModel reviewsModel);

    void deleteReview(Long reviewId);
}
