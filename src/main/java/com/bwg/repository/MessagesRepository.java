package com.bwg.repository;

import com.bwg.domain.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Long> {
    List<Messages> findAllBySender_UserId(Long userId);

}