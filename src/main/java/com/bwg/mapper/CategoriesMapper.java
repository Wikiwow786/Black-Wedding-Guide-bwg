package com.bwg.mapper;

import com.bwg.domain.Categories;
import com.bwg.model.CategoriesModel;
import com.bwg.model.ServicesModel;
import com.bwg.model.TagModel;
import com.bwg.projection.CategoriesProjection;
import com.bwg.projection.ServicesProjection;
import com.bwg.projection.TagsProjection;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CategoriesMapper {

    public static CategoriesModel toModel(CategoriesProjection projection, List<ServicesProjection> services, List<TagsProjection> tags) {
        List<ServicesModel> serviceModels = services.stream()
                .map(s -> new ServicesModel(
                        s.getServiceId(),
                        s.getServiceName(),
                        s.getPriceMin(),
                        s.getPriceMax(),
                        s.getAvailability(),
                        s.getCategoryId()
                ))
                .toList();

        Set<TagModel> tagModels = tags.stream()
                .map(t -> new TagModel(t.getTagId(), t.getName(),t.getStatus(),t.getCreatedAt()))
                .collect(Collectors.toSet());

        return new CategoriesModel(
                projection.getCategoryId(),
                projection.getUCategoryId(),
                projection.getCategoryName(),
                projection.getCreatedAt(),
                projection.getUpdatedAt(),
                serviceModels,
                tagModels
        );
    }
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

}

