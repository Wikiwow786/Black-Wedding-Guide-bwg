package com.bwg.repository;

import com.bwg.domain.WeddingProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeddingProfileRepository extends JpaRepository<WeddingProfile,Long> {
    Optional<WeddingProfile> findByUser_UserId(Long userId);
}
