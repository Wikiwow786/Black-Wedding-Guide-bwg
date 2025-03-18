package com.bwg.repository;

import com.bwg.domain.Categories;
import com.bwg.projection.CategoriesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long>, QuerydslPredicateExecutor<Categories> {
    @Query(
            value = "SELECT DISTINCT c.categoryId AS categoryId, c.uCategoryId AS uCategoryId, c.categoryName AS categoryName, c.createdAt AS createdAt, c.updatedAt AS updatedAt " +
                    "FROM Categories c " +
                    "LEFT JOIN c.tags t " +
                    "WHERE (:search IS NULL OR :search = '' OR " +
                    "LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                    "LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(DISTINCT c.categoryId) " +
                    "FROM Categories c " +
                    "LEFT JOIN c.tags t " +
                    "WHERE (:search IS NULL OR :search = '' OR " +
                    "LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                    "LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<CategoriesProjection> findCategories(@Param("search") String search, Pageable pageable);


    @Query("SELECT c.categoryId AS categoryId,c.uCategoryId, c.categoryName AS categoryName, c.createdAt AS createdAt, c.updatedAt AS updatedAt " +
            "FROM Categories c " +
    "WHERE c.categoryId = :categoryId")
    CategoriesProjection findCategoryById(@Param("categoryId") Long categoryId);

    Categories findByCategoryNameIgnoreCase(String categoryName);












}
