package com.bwg.unit.service.impl;

import com.bwg.domain.Bookings;
import com.bwg.domain.Services;
import com.bwg.domain.Users;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.exception.UnauthorizedException;
import com.bwg.model.AuthModel;
import com.bwg.model.BookingsModel;
import com.bwg.repository.BookingsRepository;
import com.bwg.repository.ServicesRepository;
import com.bwg.repository.UsersRepository;
import com.bwg.service.impl.BookingsServiceImpl;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.bwg.unit.service.util.TestDataFactory.buildAuthModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BookingServiceImplTest {
    @Mock
    private BookingsRepository bookingsRepository;

    @Mock
    private ServicesRepository servicesRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private BookingsServiceImpl bookingsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /*@Test
    void testGetAllBookings_withSearchAndStatus_returnsPagedBookings() {
        Pageable pageable = PageRequest.of(0, 10);

        AuthModel authModel = buildAuthModel("5", "ROLE_ADMIN");

        Bookings booking = new Bookings();
        booking.setStatus(Bookings.BookingStatus.confirmed);

        Page<Bookings> bookingPage = new PageImpl<>(List.of(booking));

        when(bookingsRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(bookingPage);
        Page<Bookings> result = bookingsService.getAllBookings(
                "john", Bookings.BookingStatus.confirmed, authModel, pageable
        );
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(Bookings.BookingStatus.confirmed, result.getContent().get(0).getStatus());
    }

    @Test
    void testGetAllBookings_withNullSearchAndStatus_returnsAll() {
        Pageable pageable = PageRequest.of(0, 5);
        AuthModel authModel = buildAuthModel("7", "ROLE_ADMIN");

        Bookings booking = new Bookings();
        booking.setStatus(Bookings.BookingStatus.cancelled);

        Page<Bookings> bookingPage = new PageImpl<>(List.of(booking));

        when(bookingsRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(bookingPage);
        Page<Bookings> result = bookingsService.getAllBookings(
                null, null, authModel, pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(Bookings.BookingStatus.cancelled, result.getContent().get(0).getStatus());
    }*/

    @Test
    void testGetAllBookings_throwsIfRoleUnauthorized() {
        Pageable pageable = PageRequest.of(0, 10);
        AuthModel authModel = buildAuthModel("1", "ROLE_UNKNOWN");
        assertThrows(UnauthorizedException.class, () -> bookingsService.getAllBookings(null, null, authModel, pageable));
        verify(bookingsRepository, never()).findAll(
                any(com.querydsl.core.types.Predicate.class),
                any(org.springframework.data.domain.Pageable.class)
        );
    }

    @Test
    void testGetAllBookings_withRoleVendor_doesNotThrow() {
        Pageable pageable = PageRequest.of(0, 5);
        AuthModel authModel = buildAuthModel("1", "ROLE_VENDOR");

        when(bookingsRepository.findAll(any(BooleanBuilder.class), eq(pageable)))
                .thenReturn(Page.empty());

        assertDoesNotThrow(() -> {
            bookingsService.getAllBookings(null, null, authModel, pageable);
        });
    }

    @Test
    void testCreateBooking_success() {
        BookingsModel bookingsModel = new BookingsModel();
        bookingsModel.setUserId(1L);
        bookingsModel.setServiceId(2L);

        Users user = new Users();
        user.setUserId(1L);

        Services service = new Services();
        service.setServiceId(2L);

        Bookings savedBooking = new Bookings();
        savedBooking.setBookingId(100L);
        savedBooking.setStatus(Bookings.BookingStatus.pending);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(servicesRepository.findById(2L)).thenReturn(Optional.of(service));
        when(bookingsRepository.save(any(Bookings.class))).thenReturn(savedBooking);
        Bookings result = bookingsService.createBooking(bookingsModel, buildAuthModel("1", "USER"));
        assertNotNull(result);
        assertEquals(100L, result.getBookingId());
        assertEquals(Bookings.BookingStatus.pending, result.getStatus());
    }

    @Test
    void testCreateBooking_userNotFound_throwsException() {
        BookingsModel bookingsModel = new BookingsModel();
        bookingsModel.setUserId(1L);
        bookingsModel.setServiceId(2L);

        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingsService.createBooking(bookingsModel, buildAuthModel("1", "USER")));

        verify(bookingsRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_serviceNotFound_throwsException() {
        BookingsModel bookingsModel = new BookingsModel();
        bookingsModel.setUserId(1L);
        bookingsModel.setServiceId(2L);

        Users user = new Users();
        user.setUserId(1L);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(servicesRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingsService.createBooking(bookingsModel, buildAuthModel("1", "USER")));

        verify(bookingsRepository, never()).save(any());
    }

    @Test
    void testUpdateBooking_successfullyUpdatesBooking() {
        Long bookingId = 1L;

        BookingsModel bookingsModel = new BookingsModel();
        bookingsModel.setStatus(Bookings.BookingStatus.confirmed);

        Bookings existingBooking = new Bookings();
        existingBooking.setBookingId(bookingId);
        existingBooking.setStatus(Bookings.BookingStatus.pending);

        Bookings savedBooking = new Bookings();
        savedBooking.setBookingId(bookingId);
        savedBooking.setStatus(Bookings.BookingStatus.confirmed);

        when(bookingsRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingsRepository.save(any(Bookings.class))).thenReturn(savedBooking);

        Bookings result = bookingsService.updateBooking(bookingId, bookingsModel);
        assertNotNull(result);
        assertEquals(Bookings.BookingStatus.confirmed, result.getStatus());
        assertEquals(bookingId, result.getBookingId());
    }

    @Test
    void testUpdateBooking_throwsIfBookingNotFound() {
        Long bookingId = 99L;
        BookingsModel bookingsModel = new BookingsModel();
        bookingsModel.setStatus(Bookings.BookingStatus.cancelled);
        when(bookingsRepository.findById(bookingId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingsService.updateBooking(bookingId, bookingsModel);
        });

        verify(bookingsRepository, never()).save(any());
    }

    @Test
    void testUpdateBooking_throwsIfBookingIdNull() {
        BookingsModel bookingsModel = new BookingsModel();
        bookingsModel.setStatus(Bookings.BookingStatus.cancelled);
        assertThrows(NullPointerException.class, () -> {
            bookingsService.updateBooking(null, bookingsModel);
        });
    }

    @Test
    void testDeleteBooking_callsRepositoryDeleteById() {
        Long bookingId = 1L;
        bookingsService.deleteBooking(bookingId);
        verify(bookingsRepository, times(1)).deleteById(bookingId);
    }

}
