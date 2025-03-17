package com.bwg.service.impl;

import com.bwg.domain.QServices;
import com.bwg.domain.Services;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.ServicesModel;
import com.bwg.repository.CategoriesRepository;
import com.bwg.repository.ServicesRepository;
import com.bwg.repository.VendorsRepository;
import com.bwg.service.ServicesService;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class ServicesServiceImpl implements ServicesService {

    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private VendorsRepository vendorsRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;

    @Override
    public Page<Services> getAllServices(String search,Long vendorId,Long categoryId, Double priceStart, Double priceEnd, Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Services", this);
        BooleanBuilder filter = new BooleanBuilder();
        if (StringUtils.isNotBlank(search)) {
            filter.and(QServices.services.serviceName.containsIgnoreCase(search))
                    .or(QServices.services.tags.any().name.containsIgnoreCase(search));
        }
        if (priceStart != null) {
            filter.and(QServices.services.priceMin.goe(priceStart));
        }
        if (priceEnd != null) {
            filter.and(QServices.services.priceMax.loe(priceEnd));
        }
        if(vendorId != null){
            filter.and(QServices.services.vendor.vendorId.eq(vendorId));
        }
        if(categoryId != null){
            filter.and(QServices.services.category.categoryId.eq(categoryId));
        }
        return servicesRepository.findAll(filter, pageable);
    }

    @Override
    public Services getServiceById(Long serviceId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching Service by Id {0}", serviceId);
        return servicesRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Not Found"));
    }

    @Override
    public Services createService(ServicesModel servicesModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Service..."), this);

        Services services = new Services();

        BeanUtils.copyProperties(servicesModel, services);

        services.setVendor(vendorsRepository.findById(servicesModel.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found")));
        services.setCategory(categoriesRepository.findById(servicesModel.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        services.setCreatedAt(OffsetDateTime.now());
        return servicesRepository.save(services);
    }

    @Override
    public Services updateService(Long serviceId, ServicesModel servicesModel) {
        Objects.requireNonNull(serviceId, "service ID cannot be null");

        info(LOG_SERVICE_OR_REPOSITORY, format("Update service information for service Id {0} ", serviceId), this);

        var service = servicesRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        BeanUtils.copyProperties(servicesModel, service, "serviceId", "categoryId", "vendorId", "createdAt");

        service.setUpdatedAt(OffsetDateTime.now());
        return servicesRepository.save(service);
    }

    @Override
    public void deleteService(Long serviceId) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Delete Service information for Service Id {0} ", serviceId), this);
        servicesRepository.deleteById(serviceId);
    }
}
