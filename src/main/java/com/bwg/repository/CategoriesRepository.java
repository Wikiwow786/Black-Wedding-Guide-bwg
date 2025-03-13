package com.bwg.repository;

import com.bwg.domain.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long>, QuerydslPredicateExecutor<Categories> {
    @EntityGraph(attributePaths = {"services", "tags"})
    @Query("SELECT c FROM Categories c WHERE c.categoryId IN :categoryIds")
    List<Categories> findAllByCategoryIdIn(List<Long> categoryIds);

    @Query("SELECT c.categoryId FROM Categories c")
    Page<Long> findAllCategoryIds(Pageable pageable);


}
