package com.bwg.model;

import com.bwg.config.OffsetDateTimeCustomSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CategoriesModel(
        Long categoryId,
        @JsonIgnore
        String uCategoryId,
        String categoryName,
        @JsonSerialize(using = OffsetDateTimeCustomSerializer.class)
        OffsetDateTime createdAt,
        @JsonIgnore
        OffsetDateTime updatedAt,
        List<ServicesModel> services,
        Set<TagModel> tags
) {}
