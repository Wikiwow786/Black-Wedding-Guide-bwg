package com.bwg.service;

import com.bwg.domain.Vendors;
import com.bwg.model.VendorsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VendorsService {
    Page<VendorsModel> getAllVendors(String search,Pageable pageable);

    Vendors getVendorById(Long vendorId);

    Vendors createVendor(VendorsModel vendorsModel);

    Vendors updateVendor(Long vendorId, VendorsModel vendorsModel);

    void deleteVendor(Long vendorId);
}
