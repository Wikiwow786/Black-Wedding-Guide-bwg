package com.bwg.repository;

import com.bwg.domain.Vendors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorsRepository extends JpaRepository<Vendors, Long> {
    boolean existsByUser_UserId(Long userId);
}
