package com.bwg.restapi;

import com.bwg.domain.Bookings;
import com.bwg.model.AuthModel;
import com.bwg.model.BookingsModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.BookingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.bwg.util.RoleUtil.extractUserRole;

@RestController
@RequestMapping("/bookings")
public class BookingsController {

    @Autowired
    private BookingsService bookingsService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN' , 'ROLE_VENDOR' , 'ROLE_OWNER', 'ROLE_COUPLE')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<BookingsModel>> getAllBookings(@RequestParam(required = false) String search, @RequestParam(required = false) Bookings.BookingStatus status, @AuthPrincipal AuthModel authModel, Pageable pageable) {
        return ResponseEntity.ok(bookingsService.getAllBookings(search, status, authModel, pageable).map(BookingsModel::new));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN' , 'ROLE_VENDOR' , 'ROLE_OWNER')")
    @GetMapping(value = "/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingsModel> getBookingsById(@PathVariable(value = "bookingId") final Long bookingId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new BookingsModel(bookingsService.getBookingById(bookingId, authModel)));
    }


    @PreAuthorize("hasAuthority('ROLE_COUPLE')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingsModel> createBooking(@RequestBody BookingsModel bookingsModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new BookingsModel(bookingsService.createBooking(bookingsModel, authModel)));
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
        return ResponseEntity.noContent().build();
    }
}
