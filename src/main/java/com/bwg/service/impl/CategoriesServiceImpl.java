package com.bwg.service.impl;

import com.bwg.domain.Categories;
import com.bwg.exception.ResourceAlreadyExistsException;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.mapper.CategoriesMapper;
import com.bwg.model.CategoriesModel;

import com.bwg.repository.CategoriesRepository;
import com.bwg.service.CategoriesService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.*;
import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;


    @Override
    public Page<CategoriesModel> getAllCategories(String search, Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Categories", this);
        return categoriesRepository.fetchCategoriesWithServicesAndTags(search, pageable);
    }


    @Override
    public CategoriesModel getCategoryById(Long categoryId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching Category by Id {}", categoryId);
        Categories categories = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return CategoriesMapper.toModel(categories);

    }

    @Override
    public CategoriesModel createCategory(CategoriesModel categoriesModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Category..."), this);

        if(null != categoriesRepository.findByCategoryNameIgnoreCase(categoriesModel.categoryName())){
            throw new ResourceAlreadyExistsException("Category name already exists.");
        }
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
        Categories categories = categoriesRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + categoryId + " not found"));
        categoriesRepository.delete(categories);
        info(LOG_SERVICE_OR_REPOSITORY, String.format("Category with ID %d deleted successfully", categoryId), this);
    }

}