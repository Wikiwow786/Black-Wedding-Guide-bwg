package com.bwg.service;

import com.bwg.domain.Services;
import com.bwg.model.ServicesModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicesService {

    Page<Services> getAllServices(String search,String tagName,String location, Integer rating,  Long vendorId, Long categoryId, Double priceStart, Double priceEnd, Pageable pageable);

    Services getServiceById(Long serviceId);

    Services createService(ServicesModel servicesModel);

    Services updateService(Long serviceId, ServicesModel servicesModel);

    void deleteService(Long serviceId);
}
