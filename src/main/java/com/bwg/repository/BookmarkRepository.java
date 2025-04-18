package com.bwg.repository;

import com.bwg.domain.Bookmarks;
import com.bwg.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmarks,Long>, QuerydslPredicateExecutor<Bookmarks> {
    List<Bookmarks> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
    @Query("SELECT b.entityId FROM Bookmarks b WHERE b.userId = :userId AND b.entityType = :type AND b.entityId IN :ids")
    List<Long> findEntityIdsByUserIdAndType(Long userId, Media.EntityType type, List<Long> ids);
    Bookmarks findByEntityId(Long entityId);
}
