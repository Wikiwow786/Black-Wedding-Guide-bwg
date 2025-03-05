package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.ReviewsModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.ReviewsService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<ReviewsModel>> getAllReviews() {
        return ResponseEntity.ok(reviewsService.getAllReviews().stream().map(ReviewsModel::new).toList());
    }

    @PermitAll
    @GetMapping(value = "/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewsModel> getReviewsById(@PathVariable(value = "reviewId") final Long reviewId) {
        return ResponseEntity.ok(new ReviewsModel(reviewsService.getReviewById(reviewId)));
    }

    @PreAuthorize("hasAuthority('ROLE_COUPLE')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewsModel> createReview(@RequestBody ReviewsModel reviewsModel,@AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new ReviewsModel(reviewsService.createReview(reviewsModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_VENDOR')")
    @PutMapping(value = "/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewsModel> updateReview(@PathVariable(value = "reviewId") final Long reviewId,
                                                     @RequestBody ReviewsModel reviewsModel,@AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new ReviewsModel(reviewsService.updateReview(reviewId, reviewsModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_VENDOR')")
    @DeleteMapping(value = "/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteReview(@PathVariable(value = "reviewId") final Long reviewId,@AuthPrincipal AuthModel authModel) {
        reviewsService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}
