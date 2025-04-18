package com.bwg.model;

import com.bwg.config.OffsetDateTimeCustomSerializer;
import com.bwg.domain.WeddingProfile;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record WeddingProfileModel(
        Long weddingProfileId,
        Long userId,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate weddingDate,
        Integer guestCount,
        BigDecimal totalBudget,
        String uWeddingId,
        @JsonSerialize(using = OffsetDateTimeCustomSerializer.class) OffsetDateTime createdAt,
        @JsonSerialize(using = OffsetDateTimeCustomSerializer.class)OffsetDateTime updatedAt
) {
    public WeddingProfileModel(WeddingProfile profile) {
        this(
                profile.getWeddingProfileId(),
                profile.getUser() != null ? profile.getUser().getUserId() : null,
                profile.getWeddingDate(),
                profile.getGuestCount(),
                profile.getTotalBudget(),
                profile.getuWeddingId(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
