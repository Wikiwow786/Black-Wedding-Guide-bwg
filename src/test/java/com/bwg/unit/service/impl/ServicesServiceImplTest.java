package com.bwg.unit.service.impl;

import com.bwg.domain.Categories;
import com.bwg.domain.Services;
import com.bwg.domain.Vendors;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.ServicesModel;
import com.bwg.repository.CategoriesRepository;
import com.bwg.repository.ServicesRepository;
import com.bwg.repository.VendorsRepository;
import com.bwg.service.impl.ServicesServiceImpl;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ServicesServiceImplTest {
    @Mock
    private ServicesRepository servicesRepository;

    @Mock
    VendorsRepository vendorsRepository;

    @Mock
    CategoriesRepository categoriesRepository;

    @InjectMocks
    private ServicesServiceImpl servicesService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetAllServices_withFilters_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Services mockService = new Services();
        mockService.setServiceName("Photography");

        Page<Services> mockPage = new PageImpl<>(List.of(mockService));

        when(servicesRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(mockPage);

        Page<ServicesModel> result = servicesService.getAllServices(
                "photo",
                "wedding",
                "New York",
                4,
                101L,
                202L,
                500.0,
                1500.0,
                pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Photography", result.getContent().get(0).getServiceName());
    }

    @Test
    void testGetAllServices_withNullFilters_returnsAll() {
        Pageable pageable = PageRequest.of(0, 5);

        Services service = new Services();
        service.setServiceName("DJ");

        Page<Services> mockPage = new PageImpl<>(List.of(service));

        when(servicesRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(mockPage);
        Page<ServicesModel> result = servicesService.getAllServices(
                null, null, null, null, null, null, null, null, pageable
        );
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("DJ", result.getContent().get(0).getServiceName());
    }

    @Test
    void testGetServiceById_returnsServiceIfFound() {
        Long serviceId = 1L;
        Services service = new Services();
        service.setServiceId(serviceId);
        service.setServiceName("Wedding Photography");

        when(servicesRepository.findById(serviceId)).thenReturn(Optional.of(service));

        Services result = servicesService.getServiceById(serviceId);

        assertNotNull(result);
        assertEquals("Wedding Photography", result.getServiceName());
    }

    @Test
    void testGetServiceById_throwsIfNotFound() {
        Long serviceId = 999L;
        when(servicesRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> servicesService.getServiceById(serviceId));
    }

    @Test
    void testCreateService_successfullyCreatesService() {

        ServicesModel servicesModel = new ServicesModel();
        servicesModel.setServiceName("Photography");
        servicesModel.setVendorId(1L);
        servicesModel.setCategoryId(2L);

        Vendors vendor = new Vendors();
        vendor.setVendorId(1L);

        Categories category = new Categories();
        category.setCategoryId(2L);

        Services savedService = new Services();
        savedService.setServiceId(10L);
        savedService.setServiceName("Photography");
        when(vendorsRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(categoriesRepository.findById(2L)).thenReturn(Optional.of(category));
        when(servicesRepository.save(any(Services.class))).thenReturn(savedService);
        Services result = servicesService.createService(servicesModel);
        assertNotNull(result);
        assertEquals("Photography", result.getServiceName());
        assertEquals(10L, result.getServiceId());
    }

    @Test
    void testCreateService_throwsIfVendorNotFound() {
        ServicesModel servicesModel = new ServicesModel();
        servicesModel.setServiceName("DJ");
        servicesModel.setVendorId(5L);
        servicesModel.setCategoryId(2L);
        when(vendorsRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicesService.createService(servicesModel));
    }

    @Test
    void testCreateService_throwsIfCategoryNotFound() {
        ServicesModel servicesModel = new ServicesModel();
        servicesModel.setServiceName("Catering");
        servicesModel.setVendorId(1L);
        servicesModel.setCategoryId(999L);
        Vendors vendor = new Vendors();
        vendor.setVendorId(1L);
        when(vendorsRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(categoriesRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicesService.createService(servicesModel));
    }

    @Test
    void testUpdateService_successfullyUpdatesService() {
        Long serviceId = 1L;

        ServicesModel servicesModel = new ServicesModel();
        servicesModel.setServiceName("Updated Photography");

        Services existingService = new Services();
        existingService.setServiceId(serviceId);
        existingService.setServiceName("Old Name");
        Services savedService = new Services();
        savedService.setServiceId(serviceId);
        savedService.setServiceName("Updated Photography");
        when(servicesRepository.findById(serviceId)).thenReturn(Optional.of(existingService));
        when(servicesRepository.save(any(Services.class))).thenReturn(savedService);

        Services result = servicesService.updateService(serviceId, servicesModel);
        assertNotNull(result);
        assertEquals(serviceId, result.getServiceId());
        assertEquals("Updated Photography", result.getServiceName());
    }

    @Test
    void testUpdateService_throwsIfServiceNotFound() {
        Long serviceId = 999L;
        ServicesModel servicesModel = new ServicesModel();
        servicesModel.setServiceName("Non-existent");
        when(servicesRepository.findById(serviceId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicesService.updateService(serviceId, servicesModel));
    }

    @Test
    void testUpdateService_throwsIfServiceIdNull() {
        ServicesModel servicesModel = new ServicesModel();
        servicesModel.setServiceName("Something");
        assertThrows(NullPointerException.class, () -> servicesService.updateService(null, servicesModel));
    }

    @Test
    void testDeleteService_successfullyDeletesService() {
        Long serviceId = 1L;
        Services mockService = new Services();
        mockService.setServiceId(serviceId);
        when(servicesRepository.findById(serviceId)).thenReturn(Optional.of(mockService));
        servicesService.deleteService(serviceId);
        verify(servicesRepository, times(1)).delete(mockService);
    }

    @Test
    void testDeleteService_throwsIfServiceNotFound() {
        Long serviceId = 999L;
        when(servicesRepository.findById(serviceId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicesService.deleteService(serviceId));
        verify(servicesRepository, never()).delete(any());
    }

}

