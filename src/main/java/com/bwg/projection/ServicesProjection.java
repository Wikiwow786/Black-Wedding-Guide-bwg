package com.bwg.projection;

public interface ServicesProjection {
    Long getServiceId();
    String getServiceName();
    Double getPriceMin();
    Double getPriceMax();
    String getAvailability();
    Long getCategoryId(); // For mapping services to categories
}

