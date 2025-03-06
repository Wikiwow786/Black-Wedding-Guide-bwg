package com.bwg.service;

import com.bwg.domain.Payments;
import com.bwg.model.PaymentsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentsService {
    Page<Payments> getAllPayments(Pageable pageable);

    Payments getPaymentById(Long paymentId);

    Payments createPayment(PaymentsModel paymentsModel);

    Payments updatePayment(Long paymentId, PaymentsModel paymentsModel);
}
