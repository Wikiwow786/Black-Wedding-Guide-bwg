package com.bwg.projection;

import java.time.OffsetDateTime;

public interface TagsProjection {
    Long getTagId();
    String getName();
    String getStatus();
    OffsetDateTime getCreatedAt();
}
