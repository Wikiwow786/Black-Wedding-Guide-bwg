package com.bwg.unit.service.impl;

import com.bwg.domain.Users;
import com.bwg.domain.Vendors;
import com.bwg.exception.ResourceAlreadyExistsException;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.VendorsModel;
import com.bwg.repository.UsersRepository;
import com.bwg.repository.VendorsRepository;
import com.bwg.service.impl.VendorsServiceImpl;
import com.bwg.util.SecurityUtils;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.bwg.unit.service.util.TestDataFactory.buildAuthModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class VendorsServiceImplTest {
    @Mock
    private VendorsRepository vendorsRepository;
    @Mock
    private UsersRepository usersRepository;
    @InjectMocks
    private VendorsServiceImpl vendorsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllVendors_withSearchFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        String search = "new york";

        Users mockUser = new Users();
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        Vendors vendor = new Vendors();
        vendor.setBusinessName("NY Photography");
        vendor.setLocation("New York");
        vendor.setUser(mockUser);

        Page<Vendors> vendorPage = new PageImpl<>(List.of(vendor));
        when(vendorsRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(vendorPage);

        AuthModel auth = buildAuthModel("123", "ROLE_ADMIN");

        Page<VendorsModel> result = vendorsService.getAllVendors(search, auth, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("NY Photography", result.getContent().get(0).getBusinessName());
    }

    @Test
    void testGetAllVendors_withoutSearchFilters() {
        Pageable pageable = PageRequest.of(0, 5);

        Users mockUser = new Users();
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        Vendors vendor = new Vendors();
        vendor.setBusinessName("Elegant Decor");
        vendor.setLocation("Chicago");
        vendor.setUser(mockUser);

        Page<Vendors> vendorPage = new PageImpl<>(List.of(vendor));
        when(vendorsRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(vendorPage);

        AuthModel auth = buildAuthModel("321", "ROLE_ADMIN");

        Page<VendorsModel> result = vendorsService.getAllVendors(null, auth, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Elegant Decor", result.getContent().get(0).getBusinessName());
    }

    @Test
    void testGetVendorById_returnsVendor() {
        Long vendorId = 1L;

        Vendors vendor = new Vendors();
        vendor.setVendorId(vendorId);
        vendor.setBusinessName("Elegant Events");

        when(vendorsRepository.findById(vendorId)).thenReturn(Optional.of(vendor));

        Vendors result = vendorsService.getVendorById(vendorId);

        assertNotNull(result);
        assertEquals(vendorId, result.getVendorId());
        assertEquals("Elegant Events", result.getBusinessName());
    }

    @Test
    void testGetVendorById_throwsIfNotFound() {
        Long vendorId = 404L;
        when(vendorsRepository.findById(vendorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vendorsService.getVendorById(vendorId));

        verify(vendorsRepository).findById(vendorId);
    }

    @Test
    void testCreateVendor_successfullyCreatesVendor() {

        VendorsModel vendorsModel = new VendorsModel();
        vendorsModel.setUserId(10L);
        vendorsModel.setBusinessName("Sugar & Lace Bakery");

        when(vendorsRepository.existsByUser_UserId(10L)).thenReturn(false);

        Users mockUser = new Users();
        mockUser.setUserId(10L);

        when(usersRepository.findById(10L)).thenReturn(Optional.of(mockUser));

        Vendors savedVendor = new Vendors();
        savedVendor.setVendorId(1L);
        savedVendor.setBusinessName("Sugar & Lace Bakery");

        when(vendorsRepository.save(any(Vendors.class))).thenReturn(savedVendor);

        Vendors result = vendorsService.createVendor(vendorsModel);

        assertNotNull(result);
        assertEquals("Sugar & Lace Bakery", result.getBusinessName());
        assertEquals(1L, result.getVendorId());

        verify(vendorsRepository).save(any(Vendors.class));
    }


    @Test
    void testCreateVendor_throwsIfVendorAlreadyExists() {
        VendorsModel vendorsModel = new VendorsModel();
        vendorsModel.setUserId(10L);

        when(vendorsRepository.existsByUser_UserId(10L)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> vendorsService.createVendor(vendorsModel));

        verify(vendorsRepository, never()).save(any());
    }

    @Test
    void testCreateVendor_throwsIfUserNotFound() {
        VendorsModel vendorsModel = new VendorsModel();
        vendorsModel.setUserId(10L);
        vendorsModel.setBusinessName("Dream Weddings");

        when(vendorsRepository.existsByUser_UserId(10L)).thenReturn(false);
        when(usersRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> vendorsService.createVendor(vendorsModel));

        verify(vendorsRepository, never()).save(any());
    }

    @Test
    void testUpdateVendor_successfullyUpdatesVendor() {
        Long vendorId = 1L;

        VendorsModel updateModel = new VendorsModel();
        updateModel.setBusinessName("Updated Name");

        AuthModel authModel = buildAuthModel("1", "ROLE_VENDOR");

        Vendors existingVendor = new Vendors();
        existingVendor.setVendorId(vendorId);
        existingVendor.setBusinessName("Old Name");

        Vendors savedVendor = new Vendors();
        savedVendor.setVendorId(vendorId);
        savedVendor.setBusinessName("Updated Name");

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(() -> SecurityUtils.checkOwnerOrVendor("1", authModel))
                    .thenAnswer(invocation -> null);

            when(vendorsRepository.findById(vendorId)).thenReturn(Optional.of(existingVendor));
            when(vendorsRepository.save(any(Vendors.class))).thenReturn(savedVendor);

            Vendors result = vendorsService.updateVendor(vendorId, updateModel, authModel);

            assertNotNull(result);
            assertEquals("Updated Name", result.getBusinessName());

            verify(vendorsRepository).save(existingVendor);
            securityMock.verify(() -> SecurityUtils.checkOwnerOrVendor("1", authModel), times(1));
        }
    }
    @Test
    void testUpdateVendor_throwsIfVendorNotFound() {
        Long vendorId = 404L;
        VendorsModel updateModel = new VendorsModel();
        AuthModel authModel = buildAuthModel("404", "ROLE_VENDOR");

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(() -> SecurityUtils.checkOwnerOrVendor("404", authModel))
                    .thenAnswer(invocation -> null);

            when(vendorsRepository.findById(vendorId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> vendorsService.updateVendor(vendorId, updateModel, authModel));

            verify(vendorsRepository, never()).save(any());
        }
    }

    @Test
    void testUpdateVendor_throwsIfIdIsNull() {
        VendorsModel updateModel = new VendorsModel();
        AuthModel authModel = buildAuthModel("1", "ROLE_VENDOR");

        assertThrows(NullPointerException.class, () -> vendorsService.updateVendor(null, updateModel, authModel));
    }



    @Test
    void testDeleteVendor_successfullyDeletesVendor() {
        Long vendorId = 1L;
        AuthModel authModel = buildAuthModel("1", "ROLE_VENDOR");

        Vendors existingVendor = new Vendors();
        existingVendor.setVendorId(vendorId);

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(() -> SecurityUtils.checkOwnerOrVendor("1", authModel))
                    .thenAnswer(invocation -> null);

            when(vendorsRepository.findById(vendorId)).thenReturn(Optional.of(existingVendor));
            vendorsService.deleteVendor(vendorId, authModel);
            verify(vendorsRepository).findById(vendorId);
            verify(vendorsRepository).delete(existingVendor);
            verify(vendorsRepository).flush();
            securityMock.verify(() -> SecurityUtils.checkOwnerOrVendor("1", authModel), times(1));
        }
    }

    @Test
    void testDeleteVendor_throwsIfVendorNotFound() {
        Long vendorId = 404L;
        AuthModel authModel = buildAuthModel("404", "ROLE_VENDOR");

        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            securityMock.when(() -> SecurityUtils.checkOwnerOrVendor("404", authModel))
                    .thenAnswer(invocation -> null);

            when(vendorsRepository.findById(vendorId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> vendorsService.deleteVendor(vendorId, authModel));

            verify(vendorsRepository, never()).delete(any());
            verify(vendorsRepository, never()).flush();
        }
    }




}
