package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.ServicesModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.ServicesService;
import com.bwg.util.CorrelationIdHolder;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;


@RestController
@RequestMapping("/services")
public class ServicesController {

    @Autowired
    private ServicesService servicesService;

    @PermitAll
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ServicesModel>> getAllServices(@RequestParam(required = false) String search, @RequestParam(required = false) Long vendorId, @RequestParam(required = false) Long categoryId, @RequestParam(required = false) Double priceStart, @RequestParam(required = false) Double priceEnd, @AuthPrincipal AuthModel authModel, Pageable pageable) {
        return ResponseEntity.ok(servicesService.getAllServices(search, vendorId, categoryId, priceStart, priceEnd, pageable).map(ServicesModel::new));
    }

    @PermitAll
    @GetMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicesModel> getServicesById(@PathVariable(value = "serviceId") final Long serviceId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new ServicesModel(servicesService.getServiceById(serviceId)));
    }

    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicesModel> createService(@RequestBody ServicesModel servicesModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ServicesModel(servicesService.createService(servicesModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR_OWNER')")
    @PutMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicesModel> updateService(@PathVariable(value = "serviceId") final Long serviceId,
                                                       @RequestBody ServicesModel servicesModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new ServicesModel(servicesService.updateService(serviceId, servicesModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR_OWNER')")
    @DeleteMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteService(@PathVariable(value = "serviceId") final Long serviceId, @AuthPrincipal AuthModel authModel) {
        servicesService.deleteService(serviceId);
        return ResponseEntity.noContent().build();
    }
}
