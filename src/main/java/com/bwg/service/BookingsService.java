package com.bwg.service;

import com.bwg.domain.Bookings;
import com.bwg.domain.Categories;
import com.bwg.model.AuthModel;
import com.bwg.model.BookingsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingsService {
    Page<BookingsModel> getAllBookings(String search, Bookings.BookingStatus status, AuthModel authModel, Pageable pageable);

    Bookings getBookingById(Long bookingId, AuthModel authModel);

    Bookings createBooking(BookingsModel booking, AuthModel authModel);

    Bookings updateBooking(Long bookingId, BookingsModel booking);

    void deleteBooking(Long bookingId);
}
