package com.bwg.service;

import com.bwg.domain.Bookings;
import com.bwg.domain.Categories;
import com.bwg.model.BookingsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingsService {
    Page<Bookings> getAllBookings(Pageable pageable);

    Bookings getBookingById(Long bookingId);

    Bookings createBooking(BookingsModel booking);

    Bookings updateBooking(Long bookingId, BookingsModel booking);

    void deleteBooking(Long bookingId);
}
