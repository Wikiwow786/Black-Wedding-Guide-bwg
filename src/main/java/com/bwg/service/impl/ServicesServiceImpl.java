package com.bwg.service.impl;

import com.bwg.domain.Media;
import com.bwg.domain.QServices;
import com.bwg.domain.Services;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.MediaModel;
import com.bwg.model.ServicesModel;
import com.bwg.repository.CategoriesRepository;
import com.bwg.repository.MediaRepository;
import com.bwg.repository.ServicesRepository;
import com.bwg.repository.VendorsRepository;
import com.bwg.service.MediaService;
import com.bwg.service.ServicesService;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Autowired
    private MediaRepository mediaRepository;

    @Override
    public Page<ServicesModel> getAllServices(String search,String tagName,String location, Integer rating, Long vendorId, Long categoryId, Double priceStart, Double priceEnd, Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Services", this);
        BooleanBuilder filter = new BooleanBuilder();
        if (StringUtils.isNotBlank(search)) {
            filter.and(QServices.services.serviceName.containsIgnoreCase(search));
        }

        if(StringUtils.isNotBlank(tagName)){
            filter.and(QServices.services.tags.any().name.containsIgnoreCase(tagName));
        }

        if(StringUtils.isNotBlank(location)){
            filter.and(QServices.services.location.containsIgnoreCase(location));
        }

        if (priceStart != null) {
            filter.and(QServices.services.priceMin.goe(priceStart));
        }

        if (priceEnd != null) {
            filter.and(QServices.services.priceMax.loe(priceEnd));
        }

        if (vendorId != null) {
            filter.and(QServices.services.vendor.vendorId.eq(vendorId));
        }

        if (rating != null) {
            filter.and(QServices.services.reviews.any().rating.goe(rating));
        }

        if (categoryId != null) {
            filter.and(QServices.services.category.categoryId.eq(categoryId));
        }
        Page<Services> services = servicesRepository.findAll(filter, pageable);
        List<Long> serviceIds = services.stream().map(Services::getServiceId).toList();
        List<Media> mediaEntities = mediaRepository.findAllByEntityIdIn(serviceIds);

        List<MediaModel> mediaModels = mediaEntities.stream()
                .map(MediaModel::new)
                .toList();

        Map<Long, List<MediaModel>> mediaMap = mediaModels.stream()
                .collect(Collectors.groupingBy(MediaModel::getEntityId));
        List<ServicesModel> models = services.stream().map(service -> {
            ServicesModel model = new ServicesModel(service);
            model.setMedia(mediaMap.getOrDefault(service.getServiceId(), Collections.emptyList()));
            return model;
        }).toList();
        return new PageImpl<>(models, pageable, services.getTotalElements());
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

        if(servicesModel.getVendorId() != null) {
            services.setVendor(vendorsRepository.findById(servicesModel.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found")));
        }
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
        Services services = getServiceById(serviceId);
        servicesRepository.delete(services);
    }
}
