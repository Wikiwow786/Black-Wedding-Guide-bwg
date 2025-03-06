package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.ReviewsModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.ReviewsService;
import com.bwg.util.CorrelationIdHolder;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewsController {

    @Autowired
    private ReviewsService reviewsService;

    @PermitAll
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ReviewsModel>> getAllReviews(Pageable pageable, @AuthPrincipal AuthModel authModel) {
        CorrelationIdHolder.setCorrelationId(authModel.correlationId());
        return ResponseEntity.ok(reviewsService.getAllReviews(pageable).map(ReviewsModel::new));
    }

    @PermitAll
    @GetMapping(value = "/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewsModel> getReviewsById(@PathVariable(value = "reviewId") final Long reviewId, @AuthPrincipal AuthModel authModel) {
        CorrelationIdHolder.setCorrelationId(authModel.correlationId());
        return ResponseEntity.ok(new ReviewsModel(reviewsService.getReviewById(reviewId)));
    }

    @PreAuthorize("hasAuthority('ROLE_COUPLE')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewsModel> createReview(@RequestBody ReviewsModel reviewsModel, @AuthPrincipal AuthModel authModel) {
        CorrelationIdHolder.setCorrelationId(authModel.correlationId());
        return ResponseEntity.ok(new ReviewsModel(reviewsService.createReview(reviewsModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_VENDOR')")
    @PutMapping(value = "/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewsModel> updateReview(@PathVariable(value = "reviewId") final Long reviewId,
                                                     @RequestBody ReviewsModel reviewsModel, @AuthPrincipal AuthModel authModel) {
        CorrelationIdHolder.setCorrelationId(authModel.correlationId());
        return ResponseEntity.ok(new ReviewsModel(reviewsService.updateReview(reviewId, reviewsModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_VENDOR')")
    @DeleteMapping(value = "/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteReview(@PathVariable(value = "reviewId") final Long reviewId, @AuthPrincipal AuthModel authModel) {
        CorrelationIdHolder.setCorrelationId(authModel.correlationId());
        reviewsService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}
