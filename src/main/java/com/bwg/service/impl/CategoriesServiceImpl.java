package com.bwg.service.impl;

import com.bwg.domain.Categories;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.CategoriesModel;
import com.bwg.repository.CategoriesRepository;
import com.bwg.service.CategoriesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Override
    public Page<Categories> getAllCategories(Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Categories", this);
        return categoriesRepository.findAll(pageable);
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
}