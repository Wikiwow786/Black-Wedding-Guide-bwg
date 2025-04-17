package com.bwg.model;

import com.bwg.config.OffsetDateTimeCustomSerializer;
import com.bwg.domain.Services;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ServicesModel {

    private Long serviceId;
    @JsonIgnore
    private String uServiceId;
    private Long vendorId;
    private String vendorName;
    private String vendorLocation;
    private Integer vendorTotalReviews;
    private Double vendorRating;
    private Long categoryId;
    private String categoryName;
    private String serviceName;
    private String description;
    private String location;
    private Double priceMin;
    private Double priceMax;
    private String availability;
    @JsonSerialize(using = OffsetDateTimeCustomSerializer.class)
    private OffsetDateTime createdAt;
    @JsonIgnore
    private OffsetDateTime updatedAt;
    @JsonIgnore
    private List<BookingsModel> bookingsModel;
    @JsonProperty(value = "reviews")
    private List<ReviewsModel> reviewsModel;
    private Set<TagModel> tags;
    private String primaryImagePublicUrl;
    @JsonProperty(value = "media")
    private List<MediaModel> mediaModel;

    public ServicesModel() {
    }

    public ServicesModel(Services services) {
        this.serviceId = services.getServiceId();
        this.uServiceId = services.getUServiceId();
        this.vendorId = !ObjectUtils.isEmpty(services.getVendor()) ?
                services.getVendor().getVendorId() : null;
        this.vendorName = !ObjectUtils.isEmpty(services.getVendor()) ?
                services.getVendor().getBusinessName() : null;
        this.vendorLocation = !ObjectUtils.isEmpty(services.getVendor()) ?
                services.getVendor().getLocation() : null;
        this.vendorTotalReviews = !ObjectUtils.isEmpty(services.getVendor()) ?
                services.getVendor().getTotalReviews() : null;
        this.vendorRating = !ObjectUtils.isEmpty(services.getVendor()) ?
                services.getVendor().getRating() : null;
        this.categoryId = !ObjectUtils.isEmpty(services.getCategory()) ?
                services.getCategory().getCategoryId() : null;
        this.categoryName = !ObjectUtils.isEmpty(services.getCategory()) ?
                services.getCategory().getCategoryName() : null;
        this.serviceName = services.getServiceName();
        this.location = services.getLocation();
        this.description = services.getDescription();
        this.priceMin = services.getPriceMin();
        this.priceMax = services.getPriceMax();
        this.availability = services.getAvailability();
        this.createdAt = services.getCreatedAt();
        this.updatedAt = services.getUpdatedAt();
        this.bookingsModel = !ObjectUtils.isEmpty(services.getBookings()) ?
                services.getBookings().stream().map(BookingsModel::new).toList() : null;
        this.reviewsModel = !ObjectUtils.isEmpty(services.getReviews()) ?
                services.getReviews().stream().map(ReviewsModel::new).toList() : null;
        this.tags = !ObjectUtils.isEmpty(services.getTags()) ?
                services.getTags().stream().map(TagModel::new).collect(Collectors.toSet()) :  null;
    }

    public ServicesModel(Long serviceId, String serviceName, Double priceMin, Double priceMax,
                         String availability, Long categoryId) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.availability = availability;
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServicesModel that = (ServicesModel) o;
        return Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }


    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(Double priceMin) {
        this.priceMin = priceMin;
    }

    public Double getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(Double priceMax) {
        this.priceMax = priceMax;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
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

    public List<BookingsModel> getBookingsModel() {
        return bookingsModel;
    }

    public String getUServiceId() {
        return uServiceId;
    }

    public void setUServiceId(String uServiceId) {
        this.uServiceId = uServiceId;
    }

    public void setBookingsModel(List<BookingsModel> bookingsModel) {
        this.bookingsModel = bookingsModel;
    }

    public List<ReviewsModel> getReviewsModel() {
        return reviewsModel;
    }

    public void setReviewsModel(List<ReviewsModel> reviewsModel) {
        this.reviewsModel = reviewsModel;
    }

    public Set<TagModel> getTags() {
        return tags;
    }

    public void setTags(Set<TagModel> tags) {
        this.tags = tags;
    }

    public Integer getVendorTotalReviews() {
        return vendorTotalReviews;
    }

    public void setVendorTotalReviews(Integer vendorTotalReviews) {
        this.vendorTotalReviews = vendorTotalReviews;
    }

    public Double getVendorRating() {
        return vendorRating;
    }

    public void setVendorRating(Double vendorRating) {
        this.vendorRating = vendorRating;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getVendorLocation() {
        return vendorLocation;
    }

    public void setVendorLocation(String vendorLocation) {
        this.vendorLocation = vendorLocation;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getuServiceId() {
        return uServiceId;
    }

    public void setuServiceId(String uServiceId) {
        this.uServiceId = uServiceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrimaryImagePublicUrl() {
        return primaryImagePublicUrl;
    }

    public void setPrimaryImagePublicUrl(String primaryImagePublicUrl) {
        this.primaryImagePublicUrl = primaryImagePublicUrl;
    }

    public List<MediaModel> getMediaModel() {
        return mediaModel;
    }

    public void setMediaModel(List<MediaModel> mediaModel) {
        this.mediaModel = mediaModel;
    }
}