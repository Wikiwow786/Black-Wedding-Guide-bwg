package com.bwg.repository;

import com.bwg.domain.Messages;
import com.bwg.projection.MessagesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Long>, QuerydslPredicateExecutor<Messages> {
    List<Messages> findAllBySender_UserId(Long userId);

    @Query("""
    SELECT m.messageId AS messageId, 
           m.uMessageId AS uMessageId, 
           sender.userId AS senderId,
           sender.firstName AS senderFirstName, 
           sender.lastName AS senderLastName, 
           receiver.userId AS receiverId, 
           receiver.firstName AS receiverFirstName, 
           receiver.lastName AS receiverLastName, 
           m.conversationId AS conversationId, 
           m.content AS content, 
           m.sentAt AS sentAt
    FROM Messages m
    JOIN m.sender sender
    JOIN m.receiver receiver
    WHERE (:conversationId IS NULL OR m.conversationId = :conversationId)
    AND (m.sender.userId = :userId OR m.receiver.userId = :userId)
""")
    Page<MessagesProjection> findMessages(@Param("conversationId") String conversationId,
                                          @Param("userId") Long userId,
                                          Pageable pageable);


}