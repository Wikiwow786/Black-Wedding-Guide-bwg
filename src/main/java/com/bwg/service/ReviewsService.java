package com.bwg.service;

import com.bwg.domain.Reviews;
import com.bwg.model.ReviewsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewsService {
    Page<Reviews> getAllReviews(Pageable pageable);

    Reviews getReviewById(Long reviewId);

    Reviews createReview(ReviewsModel reviewsModel);

    Reviews updateReview(Long reviewId, ReviewsModel reviewsModel);

    void deleteReview(Long reviewId);
}
