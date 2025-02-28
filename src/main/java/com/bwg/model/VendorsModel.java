package com.bwg.model;

import com.bwg.domain.Vendors;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.repository.VendorsRepository;
import com.bwg.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.util.List;

public class VendorsModel {

    private Long vendorId;
    private String uVendorId;
    private Long userId;
    private String businessName;
    private String location;
    private String description;
    private Double rating;
    private Integer totalReviews;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<ServicesModel> servicesModels;

    public VendorsModel() {
    }

    public VendorsModel(Vendors vendors) {
        this.vendorId = vendors.getVendorId();
        this.uVendorId = vendors.getUVendorId();
        this.userId = vendors.getUser().getUserId();
        this.businessName = vendors.getBusinessName();
        this.location = vendors.getLocation();
        this.description = vendors.getDescription();
        this.rating = vendors.getRating();
        this.totalReviews = vendors.getTotalReviews();
        this.createdAt = vendors.getCreatedAt();
        this.updatedAt = vendors.getUpdatedAt();
        this.servicesModels = !ObjectUtils.isEmpty(vendors.getServices()) ?
                vendors.getServices().stream().map(ServicesModel::new).toList() : null;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
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

    public List<ServicesModel> getServicesModels() {
        return servicesModels;
    }

    public void setServicesModels(List<ServicesModel> servicesModels) {
        this.servicesModels = servicesModels;
    }

    public String getUVendorId() {
        return uVendorId;
    }

    public void setUVendorId(String uVendorId) {
        this.uVendorId = uVendorId;
    }
}