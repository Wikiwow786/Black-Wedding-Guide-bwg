package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.PaymentsModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.PaymentsService;
import com.bwg.util.CorrelationIdHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

    @Autowired
    private PaymentsService paymentsService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PaymentsModel>> getAllPayments(@AuthPrincipal AuthModel authModel, Pageable pageable) {
        return ResponseEntity.ok(paymentsService.getAllPayments(pageable).map(PaymentsModel::new));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @GetMapping(value = "/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentsModel> getPaymentsById(@PathVariable(value = "paymentId") final Long paymentId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new PaymentsModel(paymentsService.getPaymentById(paymentId)));
    }

    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentsModel> createPayment(@RequestBody PaymentsModel paymentsModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new PaymentsModel(paymentsService.createPayment(paymentsModel)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentsModel> updatePayment(@PathVariable(value = "paymentId") final Long paymentId,
                                                       @RequestBody PaymentsModel paymentsModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new PaymentsModel(paymentsService.updatePayment(paymentId, paymentsModel)));
    }
}
