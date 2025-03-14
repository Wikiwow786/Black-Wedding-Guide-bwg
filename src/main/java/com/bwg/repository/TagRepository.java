package com.bwg.repository;

import com.bwg.domain.Tag;
import com.bwg.projection.TagsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long>, QuerydslPredicateExecutor<Tag> {

    @Query("SELECT DISTINCT t.tagId AS tagId, t.name AS name " +
            "FROM Tag t " +
            "JOIN t.categories c " +
            "WHERE c.categoryId IN :categoryIds")
    List<TagsProjection> findTagsForCategories(@Param("categoryIds") List<Long> categoryIds);

}
