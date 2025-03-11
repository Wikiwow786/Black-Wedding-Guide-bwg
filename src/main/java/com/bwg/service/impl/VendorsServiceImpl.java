package com.bwg.service.impl;

import com.bwg.domain.QVendors;
import com.bwg.domain.Vendors;
import com.bwg.exception.ResourceAlreadyExistsException;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.VendorsModel;
import com.bwg.repository.UsersRepository;
import com.bwg.repository.VendorsRepository;
import com.bwg.service.VendorsService;
import com.querydsl.core.BooleanBuilder;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class VendorsServiceImpl implements VendorsService {

    @Autowired
    private VendorsRepository vendorsRepository;
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public Page<VendorsModel> getAllVendors(String search,Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Vendors", this);
        BooleanBuilder filter = new BooleanBuilder();
        if(StringUtils.isNotBlank(search)){
            filter.and(QVendors.vendors.location.containsIgnoreCase(search)
                    .or(QVendors.vendors.businessName.containsIgnoreCase(search)));
        }
        return vendorsRepository.findAll(filter, pageable).map(VendorsModel::new);
    }

    @Override
    public Vendors getVendorById(Long vendorId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching Vendor by Id {0}", vendorId);
        return vendorsRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Not Found"));
    }

    @Override
    public Vendors createVendor(VendorsModel vendorsModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Vendor..."), this);

        Vendors vendors = new Vendors();

        if (vendorsRepository.existsByUser_UserId(vendorsModel.getUserId())) {
            throw new ResourceAlreadyExistsException("Vendor already exists");
        }

        BeanUtils.copyProperties(vendorsModel, vendors);

        vendors.setUser(usersRepository.findById(vendorsModel.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not Found")));
        vendors.setCreatedAt(OffsetDateTime.now());
        return vendorsRepository.save(vendors);
    }

    @Override
    public Vendors updateVendor(Long vendorId, VendorsModel vendorsModel) {
        Objects.requireNonNull(vendorId, "Vendor ID cannot be null");

        info(LOG_SERVICE_OR_REPOSITORY, format("Updating user info for userId {0}", vendorId), this);

        var vendor = vendorsRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        BeanUtils.copyProperties(vendorsModel, vendor, "vendorId", "userId", "total_reviews", "createdAt");

        vendor.setUpdatedAt(OffsetDateTime.now());
        return vendorsRepository.save(vendor);
    }

    @Override
    @Transactional
    public void deleteVendor(Long vendorId) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Attempting to delete Vendor Id {0} ", vendorId), this);
        var vendor = vendorsRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Not Found"));
        vendorsRepository.delete(vendor);
        vendorsRepository.flush();
        info(LOG_SERVICE_OR_REPOSITORY, format("Deleted Vendor Id {0} successfully", vendorId), this);
    }
}
