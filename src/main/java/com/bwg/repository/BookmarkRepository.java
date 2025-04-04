package com.bwg.repository;

import com.bwg.domain.Bookmarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmarks,Long>, QuerydslPredicateExecutor<Bookmarks> {
    List<Bookmarks> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
