package com.bwg.model;

import com.bwg.config.OffsetDateTimeCustomSerializer;
import com.bwg.domain.Bookmarks;
import com.bwg.domain.Media;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.OffsetDateTime;


public record BookmarkModel(Long bookMarkId, Long userId, String title, String uBookMarkId, @JsonSerialize(using = OffsetDateTimeCustomSerializer.class)OffsetDateTime createdAt, Long entityId,
                            Media.EntityType entityType) {

    public BookmarkModel(Bookmarks bookmark) {
        this(
                bookmark.getId(),
                bookmark.getUserId(),
                bookmark.getTitle(),
                bookmark.getuBookMarkId(),
                bookmark.getCreatedAt(),
                bookmark.getEntityId(),
                bookmark.getEntityType()
        );
    }

}
