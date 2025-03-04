package com.bwg.model;

import com.bwg.domain.Media.*;
import com.bwg.domain.Media;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.repository.MediaRepository;
import com.bwg.util.BeanUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MediaModel {

    private Long mediaId;
    private String uMediaId;
    private EntityType entityType;
    private Long entityId;
    private String mediaUrl;
    private String thumbnailUrl;
    private String mimeType;
    private String title;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public MediaModel() {
    }

    public MediaModel(Media media) {
        this.mediaId = media.getMediaId();
        this.uMediaId = media.getUMediaId();
        this.entityType = media.getEntityType();
        this.entityId = media.getEntityId();
        this.mediaUrl = media.getMediaUrl();
        this.thumbnailUrl = media.getThumbnailUrl();
        this.mimeType = media.getMimeType();
        this.title = media.getTitle();
        this.createdAt = media.getCreatedAt();
        this.updatedAt = media.getUpdatedAt();
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getUMediaId() {
        return uMediaId;
    }

    public void setUMediaId(String uMediaId) {
        this.uMediaId = uMediaId;
    }
}