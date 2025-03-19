package com.bwg.mapper;

import com.bwg.domain.Categories;
import com.bwg.model.CategoriesModel;
import com.bwg.model.ServicesModel;
import com.bwg.model.TagModel;

import jakarta.persistence.Tuple;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class CategoriesMapper {

    public static CategoriesModel toModel(Categories categories) {
        List<ServicesModel> serviceModels = categories.getServices() != null
                ? categories.getServices().stream()
                .map(ServicesModel::new)
                .toList()
                : List.of();

        Set<TagModel> tagModels = categories.getTags() != null
                ? categories.getTags().stream()
                .map(TagModel::new)
                .collect(Collectors.toSet())
                : Set.of();

        return new CategoriesModel(
                categories.getCategoryId(),
                categories.getUCategoryId(),
                categories.getCategoryName(),
                categories.getCreatedAt(),
                categories.getUpdatedAt(),
                serviceModels,
                tagModels
        );
    }


    public static CategoriesModel fromTuple(Tuple row) {
        Instant createdAtInstant = row.get("created_at", Instant.class);
        OffsetDateTime createdAt = createdAtInstant != null ? createdAtInstant.atOffset(ZoneOffset.UTC) : null;

        Instant updatedAtInstant = row.get("updated_at", Instant.class);
        OffsetDateTime updatedAt = updatedAtInstant != null ? updatedAtInstant.atOffset(ZoneOffset.UTC) : null;

        return new CategoriesModel(
                ((Number) row.get("category_id")).longValue(),
                row.get("u_category_id", String.class),
                row.get("category_name", String.class),
                createdAt,
                updatedAt,
                new ArrayList<>(),
                new HashSet<>()
        );
    }

    public static ServicesModel mapServiceFromTuple(Tuple row, Long categoryId) {
        return new ServicesModel(
                ((Number) row.get("service_id")).longValue(),
                row.get("service_name", String.class),
                row.get("price_min") != null ? ((Number) row.get("price_min")).doubleValue() : null,
                row.get("price_max") != null ? ((Number) row.get("price_max")).doubleValue() : null,
                row.get("availability", String.class),
                categoryId
        );
    }

    public static TagModel mapTagFromTuple(Tuple row) {
        Instant tagCreatedAtInstant = row.get("tag_created_at", Instant.class);
        OffsetDateTime tagCreatedAt = tagCreatedAtInstant != null ? tagCreatedAtInstant.atOffset(ZoneOffset.UTC) : null;

        return new TagModel(
                ((Number) row.get("tag_id")).longValue(),
                row.get("tag_name", String.class),
                row.get("tag_status", String.class),
                tagCreatedAt
        );
    }
}

