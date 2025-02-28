package com.bwg.restapi;

import com.bwg.model.ServicesModel;
import com.bwg.service.ServicesService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/services")
public class ServicesController {

    @Autowired
    private ServicesService servicesService;

    @PermitAll
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ServicesModel>> getAllServices(Pageable pageable) {
        return ResponseEntity.ok(servicesService.getAllServices(pageable).map(ServicesModel::new));
    }

    @PermitAll
    @GetMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicesModel> getServicesById(@PathVariable(value = "serviceId") final Long serviceId) {
        return ResponseEntity.ok(new ServicesModel(servicesService.getServiceById(serviceId)));
    }

    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicesModel> createService(@RequestBody ServicesModel servicesModel) {
        return ResponseEntity.ok(new ServicesModel(servicesService.createService(servicesModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR_OWNER')")
    @PutMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServicesModel> updateService(@PathVariable(value = "serviceId") final Long serviceId,
                                                       @RequestBody ServicesModel servicesModel) {
        return ResponseEntity.ok(new ServicesModel(servicesService.updateService(serviceId, servicesModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDOR_OWNER')")
    @DeleteMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteService(@PathVariable(value = "serviceId") final Long serviceId) {
        servicesService.deleteService(serviceId);
        return ResponseEntity.ok().build();
    }
}
