package com.bwg.model;

import com.bwg.domain.Payments;
import com.bwg.domain.Payments.*;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.repository.PaymentsRepository;
import com.bwg.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentsModel {

    private Long paymentId;
    @JsonIgnore
    private String uPaymentId;
    private Long bookingId;
    private Double amount;
    private String currency;
    private PaymentStatus status;
    private String transactionReference;
    private OffsetDateTime createdAt;

    public PaymentsModel() {
    }

    public PaymentsModel(Payments payments) {
        this.paymentId = payments.getPaymentId();
        this.uPaymentId = payments.getUPaymentId();
        this.bookingId = payments.getBooking().getBookingId();
        this.amount = payments.getAmount();
        this.currency = payments.getCurrency();
        this.status = payments.getStatus();
        this.transactionReference = payments.getTransactionReference();
        this.createdAt = payments.getCreatedAt();
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUPaymentId() {
        return uPaymentId;
    }

    public void setUPaymentId(String uPaymentId) {
        this.uPaymentId = uPaymentId;
    }
}