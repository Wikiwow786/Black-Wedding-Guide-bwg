package com.bwg.repository.impl;


import com.bwg.mapper.CategoriesMapper;
import com.bwg.model.CategoriesModel;
import com.bwg.repository.CategoriesCustomRepository;
import jakarta.persistence.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CategoriesCustomRepositoryImpl implements CategoriesCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<CategoriesModel> fetchCategoriesWithServicesAndTags(String search,String tagName, Pageable pageable) {
        String baseSql = """
    SELECT 
        c.category_id AS category_id,
        c.u_category_id AS u_category_id,
        c.category_name AS category_name,
        CAST(c.created_at AS timestamptz) AS created_at,
        CAST(c.updated_at AS timestamptz) AS updated_at,
        s.service_id AS service_id,
        s.service_name AS service_name,
        s.price_min AS price_min,
        s.price_max AS price_max,
        s.availability AS availability,
        t.tag_id AS tag_id,
        t.name AS tag_name,
        t.status AS tag_status,
        CAST(t.created_at AS timestamptz) AS tag_created_at
    FROM bwg.categories c
    LEFT JOIN bwg.services s ON c.category_id = s.category_id
    LEFT JOIN bwg.category_tags ct ON c.category_id = ct.category_id
    LEFT JOIN bwg.tags t ON t.tag_id = ct.tag_id
    WHERE (:search = '' OR LOWER(c.category_name) LIKE LOWER(CONCAT('%', :search, '%')))
    AND (:tagName = '' OR LOWER(t.name) LIKE LOWER(CONCAT('%', :tagName, '%')))
    ORDER BY c.category_name
    LIMIT :limit OFFSET :offset
""";


        Query nativeQuery = entityManager.createNativeQuery(baseSql, Tuple.class);
        nativeQuery.setParameter("search", (search == null || search.isBlank()) ? "" : search);
        nativeQuery.setParameter("tagName", (tagName == null || tagName.isBlank() ? "" : tagName));
        nativeQuery.setParameter("limit", pageable.getPageSize());
        nativeQuery.setParameter("offset", pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<Tuple> tuples = nativeQuery.getResultList();
        List<CategoriesModel> categoriesList = mapTuplesToCategories(tuples);

        long total = countCategories(search,tagName);

        return new PageImpl<>(categoriesList, pageable, total);
    }

    private long countCategories(String search,String tagName) {
        String countSql = """
            SELECT COUNT(DISTINCT c.category_id)
            FROM bwg.categories c
            LEFT JOIN bwg.category_tags ct ON c.category_id = ct.category_id
            LEFT JOIN bwg.tags t ON t.tag_id = ct.tag_id
            WHERE (:search = '' OR LOWER(c.category_name) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:tagName = '' OR LOWER(t.name) LIKE LOWER(CONCAT('%', :tagName, '%')))
        """;

        Query countQuery = entityManager.createNativeQuery(countSql);
        countQuery.setParameter("search", (search == null || search.isBlank()) ? "" : search);
        countQuery.setParameter("tagName", (tagName == null || tagName.isBlank() ? "" : tagName));
        Number count = (Number) countQuery.getSingleResult();
        return count.longValue();
    }

    private List<CategoriesModel> mapTuplesToCategories(List<Tuple> tuples) {
        Map<Long, CategoriesModel> categoriesMap = new LinkedHashMap<>();

        for (Tuple row : tuples) {
            Long categoryId = ((Number) row.get("category_id")).longValue();

            categoriesMap.computeIfAbsent(categoryId, id -> CategoriesMapper.fromTuple(row));

            CategoriesModel category = categoriesMap.get(categoryId);

            if (row.get("service_id") != null) {
                category.services().add(CategoriesMapper.mapServiceFromTuple(row, categoryId));
            }

            if (row.get("tag_id") != null) {
                category.tags().add(CategoriesMapper.mapTagFromTuple(row));
            }
        }

        return new ArrayList<>(categoriesMap.values());
    }
}


