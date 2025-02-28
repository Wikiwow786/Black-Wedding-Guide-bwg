package com.bwg.model;

import com.bwg.domain.Categories;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.repository.CategoriesRepository;
import com.bwg.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriesModel {

    private Long categoryId;
    private String uCategoryId;
    private String categoryName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<ServicesModel> servicesModel;

    public CategoriesModel() {
    }

    public CategoriesModel(Categories categories) {
        this.categoryId = categories.getCategoryId();
        this.uCategoryId = categories.getUCategoryId();
        this.categoryName = categories.getCategoryName();
        this.createdAt = categories.getCreatedAt();
        this.updatedAt = categories.getUpdatedAt();
        this.servicesModel = !ObjectUtils.isEmpty(categories.getServices()) ?
                categories.getServices().stream().map(ServicesModel::new).toList() : null;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ServicesModel> getServicesModel() {
        return servicesModel;
    }

    public void setServicesModel(List<ServicesModel> servicesModel) {
        this.servicesModel = servicesModel;
    }

    public String getUCategoryId() {
        return uCategoryId;
    }

    public void setUCategoryId(String uCategoryId) {
        this.uCategoryId = uCategoryId;
    }
}