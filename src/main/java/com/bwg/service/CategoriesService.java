package com.bwg.service;

import com.bwg.domain.Categories;
import com.bwg.model.CategoriesModel;

import java.util.List;

public interface CategoriesService {
    List<Categories> getAllCategories();

    Categories getCategoryById(Long categoryId);

    Categories createCategory(CategoriesModel categoryModel);

    Categories updateCategory(Long categoryId, CategoriesModel categoriesModel);

    void deleteCategory(Long categoryId);
}
