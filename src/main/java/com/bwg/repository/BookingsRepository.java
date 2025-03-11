package com.bwg.repository;

import com.bwg.domain.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingsRepository extends JpaRepository<Bookings, Long>, QuerydslPredicateExecutor<Bookings> {
}
