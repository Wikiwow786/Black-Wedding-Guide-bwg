package com.bwg.projection;

import java.time.OffsetDateTime;

public interface CategoriesProjection {
    Long getCategoryId();
    String getUCategoryId();
    String getCategoryName();
    OffsetDateTime getCreatedAt();
    OffsetDateTime getUpdatedAt();
}

