package com.bwg.repository;

import com.bwg.domain.Categories;
import com.bwg.projection.CategoriesProjection;
import com.bwg.projection.ServicesProjection;
import com.bwg.projection.TagsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long>, QuerydslPredicateExecutor<Categories> {
    @Query("SELECT c.categoryId AS categoryId, c.categoryName AS categoryName, c.createdAt AS createdAt, c.updatedAt AS updatedAt " +
            "FROM Categories c " +
            "WHERE (:search IS NULL OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CategoriesProjection> findCategories(@Param("search") String search, Pageable pageable);












}
