package com.bwg.model;

import com.bwg.domain.*;
import com.bwg.domain.Users.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UsersModel {

    private Long userId;
    @JsonIgnore
    private String uUserId;
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String password;
    private UserRole role;
    private String phoneNumber;
    private String profilePhotoUrl;
    private OffsetDateTime createdAt;
    @JsonIgnore
    private OffsetDateTime updatedAt;
    @JsonIgnore
    private Long vendorId;
    @JsonIgnore
    private List<BookingsModel> bookingsModel;
    @JsonIgnore
    private List<ReviewsModel> reviewsModel;
    @JsonIgnore
    private List<MessagesModel> sentMessagesModel;
    @JsonIgnore
    private List<MessagesModel> receivedMessagesModel;

    public UsersModel() {
    }

    public UsersModel(Users users) {
        this.userId = users.getUserId();
        this.uUserId = users.getUUserId();
        this.firstName = users.getFirstName();
        this.lastName = users.getLastName();
        this.email = users.getEmail();
        this.password = users.getPasswordHash();
        this.role = users.getRole();
        this.phoneNumber = users.getPhoneNumber();
        this.profilePhotoUrl = users.getProfilePhotoUrl();
        this.createdAt = users.getCreatedAt();
        this.updatedAt = users.getUpdatedAt();
        this.vendorId = !ObjectUtils.isEmpty(users.getVendor()) ?
                users.getVendor().getVendorId() : null;
        this.bookingsModel = !ObjectUtils.isEmpty(users.getBookings()) ?
                users.getBookings().stream().map(BookingsModel::new).toList() : null;
        this.reviewsModel = !ObjectUtils.isEmpty(users.getReviews()) ?
                users.getReviews().stream().map(ReviewsModel::new).toList() : null;
        this.sentMessagesModel = !ObjectUtils.isEmpty(users.getSentMessages()) ?
                users.getSentMessages().stream().map(MessagesModel::new).toList() : null;
        this.receivedMessagesModel = !ObjectUtils.isEmpty(users.getReceivedMessages()) ?
                users.getReceivedMessages().stream().map(MessagesModel::new).toList() : null;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendor(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getUUserId() {
        return uUserId;
    }

    public void setUUserId(String uUserId) {
        this.uUserId = uUserId;
    }

    public List<MessagesModel> getReceivedMessagesModel() {
        return receivedMessagesModel;
    }

    public void setReceivedMessagesModel(List<MessagesModel> receivedMessagesModel) {
        this.receivedMessagesModel = receivedMessagesModel;
    }

    public List<MessagesModel> getSentMessagesModel() {
        return sentMessagesModel;
    }

    public void setSentMessagesModel(List<MessagesModel> sentMessagesModel) {
        this.sentMessagesModel = sentMessagesModel;
    }

    public List<ReviewsModel> getReviewsModel() {
        return reviewsModel;
    }

    public void setReviewsModel(List<ReviewsModel> reviewsModel) {
        this.reviewsModel = reviewsModel;
    }

    public List<BookingsModel> getBookingsModel() {
        return bookingsModel;
    }

    public void setBookingsModel(List<BookingsModel> bookingsModel) {
        this.bookingsModel = bookingsModel;
    }
}
