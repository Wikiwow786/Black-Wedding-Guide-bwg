package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.VendorsModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.VendorsService;
import com.bwg.util.CorrelationIdHolder;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendors")
public class VendorsController {

    @Autowired
    private VendorsService vendorsService;

    @PermitAll
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<VendorsModel>> getAllVendors(@RequestParam(required = false)String search, @AuthPrincipal AuthModel authModel,Pageable pageable) {
        return ResponseEntity.ok(vendorsService.getAllVendors(search,pageable).map(VendorsModel::new));
    }

    @PermitAll
    @GetMapping(value = "/{vendorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VendorsModel> getVendorsById(@PathVariable(value = "vendorId") final Long vendorId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new VendorsModel(vendorsService.getVendorById(vendorId)));
    }

    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VendorsModel> createVendor(@RequestBody VendorsModel vendorsModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new VendorsModel(vendorsService.createVendor(vendorsModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR')")
    @PutMapping(value = "/{vendorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VendorsModel> updateVendor(@PathVariable(value = "vendorId") final Long vendorId,
                                                     @RequestBody VendorsModel vendorsModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new VendorsModel(vendorsService.updateVendor(vendorId, vendorsModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR')")
    @DeleteMapping(value = "/{vendorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteVendor(@PathVariable(value = "vendorId") final Long vendorId, @AuthPrincipal AuthModel authModel) {
        vendorsService.deleteVendor(vendorId);
        return ResponseEntity.noContent().build();
    }
}