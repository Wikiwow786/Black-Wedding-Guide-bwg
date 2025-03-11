package com.bwg.model;

import com.bwg.domain.Bookings;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


import java.time.OffsetDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookingsModel {

    private Long bookingId;
    @JsonIgnore
    private String uBookingId;
    private Long userId;
    private String userName;
    private Long serviceId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    private OffsetDateTime eventDate;
    private Bookings.BookingStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    private OffsetDateTime createdAt;
    @JsonIgnore
    private OffsetDateTime updatedAt;

    public BookingsModel() {
    }

    public BookingsModel(Bookings bookings) {
        this.bookingId = bookings.getBookingId();
        this.uBookingId = bookings.getUBookingId();
        this.userId = bookings.getUser().getUserId();
        this.userName = (bookings.getUser().getFirstName() != null ? bookings.getUser().getFirstName() : "")
                + " " +
                (bookings.getUser().getLastName() != null ? bookings.getUser().getLastName() : "");


        this.serviceId = bookings.getService().getServiceId();
        this.eventDate = bookings.getEventDate();
        this.status = bookings.getStatus();
        this.createdAt = bookings.getCreatedAt();
        this.updatedAt = bookings.getUpdatedAt();
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public OffsetDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(OffsetDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Bookings.BookingStatus getStatus() {
        return status;
    }

    public void setStatus(Bookings.BookingStatus status) {
        this.status = status;
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

    public String getUBookingId() {
        return uBookingId;
    }

    public void setUBookingId(String uBookingId) {
        this.uBookingId = uBookingId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}