package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.BookingsModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.BookingsService;
import com.bwg.util.CorrelationIdHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingsController {

    @Autowired
    private BookingsService bookingsService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN' , 'ROLE_VENDOR' , 'ROLE_OWNER')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookingsModel>> getAllBookings(@AuthPrincipal AuthModel authModel, Pageable pageable) {
        CorrelationIdHolder.setCorrelationId(authModel.correlationId());
        return ResponseEntity.ok(bookingsService.getAllBookings(pageable).stream().map(BookingsModel::new).toList());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN' , 'ROLE_VENDOR' , 'ROLE_OWNER')")
    @GetMapping(value = "/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingsModel> getBookingsById(@PathVariable(value = "bookingId") final Long bookingId, @AuthPrincipal AuthModel authModel) {
        CorrelationIdHolder.setCorrelationId(authModel.correlationId());
        return ResponseEntity.ok(new BookingsModel(bookingsService.getBookingById(bookingId)));
    }


    @PreAuthorize("hasAuthority('ROLE_COUPLE')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingsModel> createBooking(@RequestBody BookingsModel bookingsModel, @AuthPrincipal AuthModel authModel) {
        CorrelationIdHolder.setCorrelationId(authModel.correlationId());
        return ResponseEntity.ok(new BookingsModel(bookingsService.createBooking(bookingsModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN' , 'ROLE_VENDOR' , 'ROLE_OWNER')")
    @PutMapping(value = "/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingsModel> updateBooking(@PathVariable(value = "bookingId") final Long bookingId,
                                                       @RequestBody BookingsModel bookingsModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new BookingsModel(bookingsService.updateBooking(bookingId, bookingsModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN' , 'ROLE_VENDOR' , 'ROLE_OWNER')")
    @DeleteMapping(value = "/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteBooking(@PathVariable(value = "bookingId") final Long bookingId, @AuthPrincipal AuthModel authModel) {
        bookingsService.deleteBooking(bookingId);
        return ResponseEntity.ok().build();
    }
}
