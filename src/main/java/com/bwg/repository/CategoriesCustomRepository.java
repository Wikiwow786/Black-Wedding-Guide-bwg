package com.bwg.repository;

import com.bwg.model.CategoriesModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoriesCustomRepository {
    Page<CategoriesModel> fetchCategoriesWithServicesAndTags(String search, Pageable pageable);
}
