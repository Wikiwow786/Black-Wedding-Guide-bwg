package com.bwg.repository;

import com.bwg.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long>, QuerydslPredicateExecutor<Tag> {

}
