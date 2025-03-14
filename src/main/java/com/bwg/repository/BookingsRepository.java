package com.bwg.repository;

import com.bwg.domain.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingsRepository extends JpaRepository<Bookings, Long>, QuerydslPredicateExecutor<Bookings> {
    Bookings findByUser_UserIdAndService_ServiceId(Long userId, Long serviceId);

    Bookings findByBookingIdAndUser_UserId(Long bookingId, Long userId);

    Optional<Bookings> findByBookingIdAndService_Vendor_User_UserId(Long bookingId, Long userId);

}
