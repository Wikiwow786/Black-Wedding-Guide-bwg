package com.bwg.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "wedding_profile", schema = "bwg")
public class WeddingProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bwg.hibernate_sequence")
    @GenericGenerator(
            name = "bwg.hibernate_sequence",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "50")
            }
    )
    private Long weddingProfileId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @Column(name = "wedding_date", nullable = false)
    private LocalDate weddingDate;
    @Column(name = "guest_count")
    private Integer guestCount;
    @Column(name = "total_budget")
    private BigDecimal totalBudget;
    @Column(name = "u_wedding_id")
    private String uWeddingId;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void setUWeddingId() {
        this.uWeddingId = StringUtils.hasText(this.getuWeddingId()) ? this.uWeddingId : (this.uWeddingId = UUID.randomUUID().toString());
    }

    public Long getWeddingProfileId() {
        return weddingProfileId;
    }

    public void setWeddingProfileId(Long weddingProfileId) {
        this.weddingProfileId = weddingProfileId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public LocalDate getWeddingDate() {
        return weddingDate;
    }

    public void setWeddingDate(LocalDate weddingDate) {
        this.weddingDate = weddingDate;
    }

    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getuWeddingId() {
        return uWeddingId;
    }

    public void setuWeddingId(String uWeddingId) {
        this.uWeddingId = uWeddingId;
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
}
