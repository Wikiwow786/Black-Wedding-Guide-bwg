package com.bwg.service.impl;

import com.bwg.domain.Categories;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.mapper.CategoriesMapper;
import com.bwg.model.CategoriesModel;
import com.bwg.model.ServicesModel;
import com.bwg.model.TagModel;
import com.bwg.projection.CategoriesProjection;
import com.bwg.projection.ServicesProjection;
import com.bwg.projection.TagsProjection;
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

        if(search == null){
            search = "";
        }

        Page<CategoriesProjection> categoriesPage = categoriesRepository.findCategories(search, pageable);
        if (categoriesPage.isEmpty()) {
            return Page.empty();
        }

        List<Long> categoryIds = categoriesPage.map(CategoriesProjection::getCategoryId).toList();
        Map<Long, List<ServicesModel>> serviceMap = mapServicesToCategories(categoryIds);

        Map<Long, Set<TagModel>> tagMap = fetchTagsMap(categoryIds);

        List<CategoriesModel> categoryModels = categoriesPage.getContent().stream()
                .map(categoryProj -> new CategoriesModel(
                        categoryProj.getCategoryId(),
                        categoryProj.getUCategoryId(),
                        categoryProj.getCategoryName(),
                        categoryProj.getCreatedAt(),
                        categoryProj.getUpdatedAt(),
                        serviceMap.getOrDefault(categoryProj.getCategoryId(), Collections.emptyList()),
                        tagMap.getOrDefault(categoryProj.getCategoryId(), Collections.emptySet())
                ))
                .toList();

        return new PageImpl<>(categoryModels, pageable, categoriesPage.getTotalElements());
    }


    @Override
    public CategoriesModel getCategoryById(Long categoryId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching Category by Id {}", categoryId);
        CategoriesProjection projection = categoriesRepository.findCategoryById(categoryId);

        if (projection == null) {
            throw new ResourceNotFoundException("Category not found");
        }

        List<ServicesProjection> services = servicesRepository.findServicesForCategories(List.of(categoryId));
        List<TagsProjection> tags = tagRepository.findTagsForCategories(List.of(categoryId));

        return CategoriesMapper.toModel(projection, services, tags);

    }

    @Override
    public CategoriesModel createCategory(CategoriesModel categoriesModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Category..."), this);

        Categories categories = new Categories();

        BeanUtils.copyProperties(categoriesModel, categories);

        categories.setCreatedAt(OffsetDateTime.now());
         return CategoriesMapper.toModel(categoriesRepository.save(categories));
    }

    @Override
    public CategoriesModel updateCategory(Long categoryId, CategoriesModel categoriesModel) {
        Objects.requireNonNull(categoryId, "categoryId ID cannot be null");

        info(LOG_SERVICE_OR_REPOSITORY, format("Update Category information for Category Id {0} ", categoryId), this);

        var category = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        BeanUtils.copyProperties(categoriesModel, category, "categoryId", "createdAt");

        category.setUpdatedAt(OffsetDateTime.now());
        return CategoriesMapper.toModel(categoriesRepository.save(category));
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

    private Map<Long, Set<TagModel>> fetchTagsMap(List<Long> categoryIds) {
        List<Object[]> results = tagRepository.findTagsForCategoryIds(categoryIds);
        Map<Long, Set<TagModel>> tagMap = new HashMap<>();

        for (Object[] row : results) {
            Long categoryId = (Long) row[0];
            Long tagId = (Long) row[1];
            String tagName = (String) row[2];
            String status = (String) row[3];
            OffsetDateTime createdAt = (OffsetDateTime) row[4];

            tagMap.computeIfAbsent(categoryId, k -> new HashSet<>())
                    .add(new TagModel(tagId, tagName,status,createdAt));
        }

        return tagMap;
    }
}