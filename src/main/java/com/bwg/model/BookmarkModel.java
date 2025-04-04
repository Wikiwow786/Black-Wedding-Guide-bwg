package com.bwg.model;

import com.bwg.config.OffsetDateTimeCustomSerializer;
import com.bwg.domain.Bookmarks;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.OffsetDateTime;

public record BookmarkModel(Long bookMarkId, Long userId, String title, String imageUrl, String uBookMarkId, @JsonSerialize(using = OffsetDateTimeCustomSerializer.class)OffsetDateTime createdAt) {

    public BookmarkModel(Bookmarks bookmark) {
        this(
                bookmark.getId(),
                bookmark.getUserId(),
                bookmark.getTitle(),
                bookmark.getImageUrl(),
                bookmark.getuBookMarkId(),
                bookmark.getCreatedAt()
        );
    }

}
