package com.bwg.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "Payments", schema = "bwg")
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bwg.hibernate_sequence")
    @GenericGenerator(name = "bwg.hibernate_sequence", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@org.hibernate.annotations.Parameter(name = "increment_size", value = "50")})
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "u_payment_id")
    private String uPaymentId;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Bookings booking;

    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double amount;

    @Column(name = "currency", nullable = false, length = 10, columnDefinition = "VARCHAR(10) DEFAULT 'USD'")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "bwg.payment_status")
    private PaymentStatus status;

    @Column(name = "transaction_reference", nullable = false, length = 255)
    private String transactionReference;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @PrePersist
    public void setUPaymentId() {
        this.uPaymentId = StringUtils.hasText(this.uPaymentId) ? this.uPaymentId : (this.uPaymentId = UUID.randomUUID().toString());
    }

    public String getUPaymentId() {
        return uPaymentId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Bookings getBooking() {
        return booking;
    }

    public void setBooking(Bookings booking) {
        this.booking = booking;
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

    // Enum for PaymentStatus
    public enum PaymentStatus {
        initiated,
        successful,
        failed,
        refunded
    }
}
