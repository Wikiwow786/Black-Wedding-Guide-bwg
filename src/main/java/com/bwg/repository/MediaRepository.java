package com.bwg.repository;

import com.bwg.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long>, QuerydslPredicateExecutor<Media> {
}
