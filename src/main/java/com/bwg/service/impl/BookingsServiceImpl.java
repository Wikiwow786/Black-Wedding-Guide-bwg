package com.bwg.service.impl;

import com.bwg.domain.Bookings;
import com.bwg.domain.QBookings;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.BookingsModel;
import com.bwg.repository.BookingsRepository;
import com.bwg.repository.ServicesRepository;
import com.bwg.repository.UsersRepository;
import com.bwg.service.BookingsService;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class BookingsServiceImpl implements BookingsService {

    @Autowired
    private BookingsRepository bookingsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ServicesRepository servicesRepository;

    @Override
    public Page<Bookings> getAllBookings(String search,Bookings.BookingStatus status,Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Users", this);
        BooleanBuilder filter = new BooleanBuilder();
        if(StringUtils.isNotBlank(search)){
            filter.and(QBookings.bookings.user.firstName.containsIgnoreCase(search))
                    .or(QBookings.bookings.user.lastName.containsIgnoreCase(search));
        }
        if(status != null){
            filter.and(QBookings.bookings.status.eq(status));
        }
        return bookingsRepository.findAll(pageable);
    }

    @Override
    public Bookings getBookingById(Long bookingId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching User by Id {0}", bookingId);
        return bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Override
    public Bookings createBooking(BookingsModel bookingsModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Booking ..."), this);

        Bookings bookings = new Bookings();

        BeanUtils.copyProperties(bookingsModel, bookings);

        bookings.setUser(usersRepository.findById(bookingsModel.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        bookings.setService(servicesRepository.findById(bookingsModel.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found")));

        bookings.setCreatedAt(OffsetDateTime.now());
        bookings.setStatus(Bookings.BookingStatus.pending);
        return bookingsRepository.save(bookings);
    }

    @Override
    public Bookings updateBooking(Long bookingId, BookingsModel bookingsModel) {
        Objects.requireNonNull(bookingId, "booking ID cannot be null");

        info(LOG_SERVICE_OR_REPOSITORY, format("Update Booking information for Booking Id {0} ", bookingId), this);

        var booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        BeanUtils.copyProperties(bookingsModel, booking, "bookingId", "userId", "serviceId", "createdAt");

        booking.setUpdatedAt(OffsetDateTime.now());
        return bookingsRepository.save(booking);
    }

    @Override
    public void deleteBooking(Long bookingId) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Delete Booking information for Booking Id {0} ", bookingId), this);
        bookingsRepository.deleteById(bookingId);
    }
}
