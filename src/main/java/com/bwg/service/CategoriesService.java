package com.bwg.service;

import com.bwg.model.CategoriesModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoriesService {
    Page<CategoriesModel> getAllCategories(String search,Pageable pageable);

    CategoriesModel getCategoryById(Long categoryId);

    CategoriesModel createCategory(CategoriesModel categoryModel);

    CategoriesModel updateCategory(Long categoryId, CategoriesModel categoriesModel);

    void deleteCategory(Long categoryId);
}
