package com.bwg.service.impl;

import com.bwg.domain.Payments;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.PaymentsModel;
import com.bwg.repository.BookingsRepository;
import com.bwg.repository.PaymentsRepository;
import com.bwg.service.PaymentsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class PaymentsServiceImpl implements PaymentsService {

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Autowired
    private BookingsRepository bookingsRepository;

    @Override
    public List<Payments> getAllPayments() {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Payments", this);
        return paymentsRepository.findAll();
    }

    @Override
    public Payments getPaymentById(Long paymentId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching Payment by Id {0}", paymentId);
        return paymentsRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Override
    public Payments createPayment(PaymentsModel paymentsModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Payment..."), this);

        Payments payments = new Payments();

        BeanUtils.copyProperties(paymentsModel, payments);
        payments.setBooking(bookingsRepository.findById(paymentsModel.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found")));

        payments.setCreatedAt(OffsetDateTime.now());
        payments.setStatus(Payments.PaymentStatus.initiated);
        return paymentsRepository.save(payments);
    }

    @Override
    public Payments updatePayment(Long paymentId, PaymentsModel paymentsModel) {
        Objects.requireNonNull(paymentId, "service ID cannot be null");

        info(LOG_SERVICE_OR_REPOSITORY, format("Update Payments information for Payment Id {0} ", paymentId), this);

        var payment = paymentsRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        BeanUtils.copyProperties(paymentsModel, payment, "paymentId", "bookingId", "amount", "currency", "createdAt");

        return paymentsRepository.save(payment);

    }
}
