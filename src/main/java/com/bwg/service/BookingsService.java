package com.bwg.service;

import com.bwg.domain.Bookings;
import com.bwg.model.BookingsModel;

import java.util.List;

public interface BookingsService {
    List<Bookings> getAllBookings();

    Bookings getBookingById(Long bookingId);

    Bookings createBooking(BookingsModel booking);

    Bookings updateBooking(Long bookingId, BookingsModel booking);

    void deleteBooking(Long bookingId);
}
