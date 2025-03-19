package com.bwg.model;

import com.bwg.config.OffsetDateTimeCustomSerializer;
import com.bwg.domain.Reviews;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.repository.ReviewsRepository;
import com.bwg.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReviewsModel {

    private Long reviewId;
    @JsonIgnore
    private String uReviewId;
    private Long userId;
    private String userName;
    private String profilePhotoUrl;
    private Long serviceId;
    private Integer rating;
    private String comment;
    @JsonSerialize(using = OffsetDateTimeCustomSerializer.class)
    private OffsetDateTime createdAt;

    public ReviewsModel() {
    }

    public ReviewsModel(Reviews reviews) {
        this.reviewId = reviews.getReviewId();
        this.uReviewId = reviews.getUReviewId();
        this.userId = reviews.getUser().getUserId();
        this.userName = (reviews.getUser().getFirstName() != null ? reviews.getUser().getFirstName() : "")
                + " " +
                (reviews.getUser().getLastName() != null ? reviews.getUser().getLastName() : "");
        this.profilePhotoUrl = reviews.getUser().getProfilePhotoUrl();
        this.serviceId = reviews.getService().getServiceId();
        this.rating = reviews.getRating();
        this.comment = reviews.getComment();
        this.createdAt = reviews.getCreatedAt();
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUReviewId() {
        return uReviewId;
    }

    public void setUReviewId(String uReviewId) {
        this.uReviewId = uReviewId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setuReviewId(String uReviewId) {
        this.uReviewId = uReviewId;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
}