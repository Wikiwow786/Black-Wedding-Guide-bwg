package com.bwg.repository;

import com.bwg.domain.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long>, QuerydslPredicateExecutor<Reviews> {
}
