package com.bwg.repository;

import com.bwg.domain.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Long>, QuerydslPredicateExecutor<Payments> {
}
