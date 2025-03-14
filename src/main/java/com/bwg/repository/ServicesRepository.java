package com.bwg.repository;

import com.bwg.domain.Services;
import com.bwg.projection.ServicesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long>, QuerydslPredicateExecutor<Services> {
    @Query("SELECT s.serviceId AS serviceId, s.serviceName AS serviceName, s.priceMin AS priceMin, " +
            "s.priceMax AS priceMax, s.availability AS availability, s.category.categoryId AS categoryId " +
            "FROM Services s " +
            "WHERE s.category.categoryId IN :categoryIds")
    List<ServicesProjection> findServicesForCategories(@Param("categoryIds") List<Long> categoryIds);
}
