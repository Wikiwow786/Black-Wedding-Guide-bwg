package com.bwg.service.impl;

import com.bwg.domain.Messages;
import com.bwg.domain.Reviews;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.ReviewsModel;
import com.bwg.repository.ReviewsRepository;
import com.bwg.repository.ServicesRepository;
import com.bwg.repository.UsersRepository;
import com.bwg.service.ReviewsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class ReviewsServiceImpl implements ReviewsService {

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ServicesRepository servicesRepository;

    @Override
    public List<Reviews> getAllReviews() {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Reviews", this);
        return reviewsRepository.findAll();
    }

    @Override
    public Reviews getReviewById(Long reviewId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching Review by Id {0}", reviewId);
        return reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Override
    public Reviews createReview(ReviewsModel reviewsModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Review..."), this);

        Reviews reviews = new Reviews();

        BeanUtils.copyProperties(reviewsModel, reviews);
        reviews.setUser(usersRepository.findById(reviewsModel.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        reviews.setService(servicesRepository.findById(reviewsModel.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found")));

        reviews.setCreatedAt(OffsetDateTime.now());
        return reviewsRepository.save(reviews);
    }

    @Override
    public Reviews updateReview(Long reviewId, ReviewsModel reviewsModel) {
        Objects.requireNonNull(reviewId, "review ID cannot be null");

        info(LOG_SERVICE_OR_REPOSITORY, format("Update Review information for Review Id {0} ", reviewId), this);

        var review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        BeanUtils.copyProperties(reviewsModel, review, "reviewId", "userId", "serviceId", "createdAt");

        return reviewsRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Delete Review information for Review Id {0} ", reviewId), this);
        reviewsRepository.deleteById(reviewId);
    }
}
