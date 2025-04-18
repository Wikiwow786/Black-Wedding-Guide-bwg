package com.bwg.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookmarks", schema = "bwg")
public class Bookmarks {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bwg.hibernate_sequence")
    @GenericGenerator(name = "bwg.hibernate_sequence", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@org.hibernate.annotations.Parameter(name = "increment_size", value = "50")})
    @Column(name = "bookmark_id", nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "title")
    private String title;

    @Column(name = "u_bookmark_id")
    private String uBookMarkId;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @Column(name = "entity_id")
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private Media.EntityType entityType;

    @PrePersist
    public void setUBookingId() {
        this.uBookMarkId = StringUtils.hasText(this.getuBookMarkId()) ? this.uBookMarkId : (this.uBookMarkId = UUID.randomUUID().toString());
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getuBookMarkId() {
        return uBookMarkId;
    }

    public void setuBookMarkId(String uBookMarkId) {
        this.uBookMarkId = uBookMarkId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Media.EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(Media.EntityType entityType) {
        this.entityType = entityType;
    }
}

