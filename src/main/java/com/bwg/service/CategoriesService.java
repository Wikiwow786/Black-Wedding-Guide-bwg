package com.bwg.service;

import com.bwg.domain.Categories;
import com.bwg.domain.Media;
import com.bwg.model.CategoriesModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriesService {
    Page<CategoriesModel> getAllCategories(String search,Pageable pageable);

    Categories getCategoryById(Long categoryId);

    Categories createCategory(CategoriesModel categoryModel);

    Categories updateCategory(Long categoryId, CategoriesModel categoriesModel);

    void deleteCategory(Long categoryId);
}
