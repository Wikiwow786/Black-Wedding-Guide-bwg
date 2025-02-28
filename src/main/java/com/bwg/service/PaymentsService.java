package com.bwg.service;

import com.bwg.domain.Payments;
import com.bwg.model.PaymentsModel;

import java.util.List;

public interface PaymentsService {
    List<Payments> getAllPayments();

    Payments getPaymentById(Long paymentId);

    Payments createPayment(PaymentsModel paymentsModel);

    Payments updatePayment(Long paymentId, PaymentsModel paymentsModel);
}
