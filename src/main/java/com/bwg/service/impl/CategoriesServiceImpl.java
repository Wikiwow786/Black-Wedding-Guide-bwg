package com.bwg.service.impl;

import com.bwg.domain.Categories;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.CategoriesModel;
import com.bwg.model.ServicesModel;
import com.bwg.model.TagModel;
import com.bwg.projection.CategoriesProjection;
import com.bwg.projection.ServicesProjection;
import com.bwg.repository.CategoriesRepository;
import com.bwg.repository.ServicesRepository;
import com.bwg.repository.TagRepository;
import com.bwg.service.CategoriesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ServicesRepository servicesRepository;

    @Override
    public Page<CategoriesModel> getAllCategories(String search, Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Categories", this);

        Page<CategoriesProjection> categoriesPage = categoriesRepository.findCategories(search, pageable);
        if (categoriesPage.isEmpty()) {
            return Page.empty();
        }

        List<Long> categoryIds = categoriesPage.map(CategoriesProjection::getCategoryId).toList();
        Map<Long, List<ServicesModel>> serviceMap = mapServicesToCategories(categoryIds);
        Set<TagModel> tagModels = fetchTags(categoryIds);

        List<CategoriesModel> categoryModels = categoriesPage.getContent().stream()
                .map(categoryProj -> new CategoriesModel(
                        categoryProj.getCategoryId(),
                        categoryProj.getCategoryName(),
                        categoryProj.getCreatedAt(),
                        categoryProj.getUpdatedAt(),
                        serviceMap.getOrDefault(categoryProj.getCategoryId(), Collections.emptyList()),
                        tagModels
                ))
                .toList();

        return new PageImpl<>(categoryModels, pageable, categoriesPage.getTotalElements());
    }


    @Override
    public Categories getCategoryById(Long categoryId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching Category by Id {0}", categoryId);
        return categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category Not Found"));
    }

    @Override
    public Categories createCategory(CategoriesModel categoriesModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Category..."), this);

        Categories categories = new Categories();

        BeanUtils.copyProperties(categoriesModel, categories);

        categories.setCreatedAt(OffsetDateTime.now());
        return categoriesRepository.save(categories);
    }

    @Override
    public Categories updateCategory(Long categoryId, CategoriesModel categoriesModel) {
        Objects.requireNonNull(categoryId, "categoryId ID cannot be null");

        info(LOG_SERVICE_OR_REPOSITORY, format("Update Category information for Category Id {0} ", categoryId), this);

        var category = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        BeanUtils.copyProperties(categoriesModel, category, "categoryId", "createdAt");

        category.setUpdatedAt(OffsetDateTime.now());
        return categoriesRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Delete Category information for Category Id {0} ", categoryId), this);
        categoriesRepository.deleteById(categoryId);
    }

    private Map<Long, List<ServicesModel>> mapServicesToCategories(List<Long> categoryIds) {
        return servicesRepository.findServicesForCategories(categoryIds).stream()
                .collect(Collectors.groupingBy(
                        ServicesProjection::getCategoryId,
                        Collectors.mapping(s -> new ServicesModel(
                                s.getServiceId(),
                                s.getServiceName(),
                                s.getPriceMin(),
                                s.getPriceMax(),
                                s.getAvailability(),
                                s.getCategoryId()
                        ), Collectors.toList())
                ));
    }

    private Set<TagModel> fetchTags(List<Long> categoryIds) {
        return tagRepository.findTagsForCategories(categoryIds).stream()
                .map(t -> new TagModel(t.getTagId(), t.getName()))
                .collect(Collectors.toSet());
    }
}