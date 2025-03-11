package com.bwg.repository;

import com.bwg.domain.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingsRepository extends JpaRepository<Bookings, Long>, QuerydslPredicateExecutor<Bookings> {
    Bookings findByUser_UserIdAndService_ServiceId(Long userId, Long serviceId);
}
