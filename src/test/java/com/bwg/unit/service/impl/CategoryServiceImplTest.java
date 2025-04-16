package com.bwg.unit.service.impl;

import com.bwg.domain.Categories;
import com.bwg.exception.ResourceAlreadyExistsException;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.mapper.CategoriesMapper;
import com.bwg.model.CategoriesModel;
import com.bwg.repository.CategoriesRepository;
import com.bwg.service.impl.CategoriesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {
    @Mock
    private CategoriesRepository categoriesRepository;
    @InjectMocks
    private CategoriesServiceImpl categoriesService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories_returnsPage() {

        Pageable pageable = PageRequest.of(0, 10);

        CategoriesModel category = new CategoriesModel(
                1L,
                "UCAT-1",
                "Venue",
                OffsetDateTime.now(),
                null,
                List.of(),
                Set.of()
        );

        Page<CategoriesModel> mockPage = new PageImpl<>(List.of(category));

        when(categoriesRepository.fetchCategoriesWithServicesAndTags("venue", "popular", pageable))
                .thenReturn(mockPage);

        Page<CategoriesModel> result = categoriesService.getAllCategories("venue", "popular", pageable);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Venue", result.getContent().get(0).categoryName());
    }

    @Test
    void testGetAllCategories_nullFilters() {
        Pageable pageable = PageRequest.of(0, 5);

        CategoriesModel category = new CategoriesModel(
                2L,
                "UCAT-2",
                "Photography",
                OffsetDateTime.now(),
                null,
                List.of(),
                Set.of()
        );

        Page<CategoriesModel> mockPage = new PageImpl<>(List.of(category));

        when(categoriesRepository.fetchCategoriesWithServicesAndTags(null, null, pageable))
                .thenReturn(mockPage);
        Page<CategoriesModel> result = categoriesService.getAllCategories(null, null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Photography", result.getContent().get(0).categoryName());
    }

    @Test
    void testGetCategoryById_returnsModel() {
        Long categoryId = 1L;
        Categories categories = new Categories();
        categories.setCategoryId(categoryId);
        categories.setCategoryName("Photography");

        CategoriesModel model = new CategoriesModel(
                categoryId,
                "UCAT-1",
                "Photography",
                OffsetDateTime.now(),
                null,
                List.of(),
                Set.of()
        );

        when(categoriesRepository.findById(categoryId)).thenReturn(Optional.of(categories));

        try (MockedStatic<CategoriesMapper> mockedStatic = mockStatic(CategoriesMapper.class)) {
            mockedStatic.when(() -> CategoriesMapper.toModel(categories)).thenReturn(model);
            CategoriesModel result = categoriesService.getCategoryById(categoryId);

            assertNotNull(result);
            assertEquals("Photography", result.categoryName());
            mockedStatic.verify(() -> CategoriesMapper.toModel(categories), times(1));
        }
    }

    @Test
    void testGetCategoryById_notFound() {
        Long categoryId = 404L;
        when(categoriesRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoriesService.getCategoryById(categoryId));

        verify(categoriesRepository, times(1)).findById(categoryId);
    }

    @Test
    void testCreateCategory_successfullyCreatesCategory() {
        // Arrange
        CategoriesModel inputModel = new CategoriesModel(
                null,
                "UCAT-1",
                "Venue",
                null,
                null,
                List.of(),
                Set.of()
        );

        Categories savedEntity = new Categories();
        savedEntity.setCategoryId(1L);
        savedEntity.setCategoryName("Venue");

        CategoriesModel expectedModel = new CategoriesModel(
                1L,
                "UCAT-1",
                "Venue",
                OffsetDateTime.now(),
                null,
                List.of(),
                Set.of()
        );

        when(categoriesRepository.findByCategoryNameIgnoreCase("Venue")).thenReturn(null);
        when(categoriesRepository.save(any(Categories.class))).thenReturn(savedEntity);

        try (MockedStatic<CategoriesMapper> mockedStatic = mockStatic(CategoriesMapper.class)) {
            mockedStatic.when(() -> CategoriesMapper.toModel(savedEntity)).thenReturn(expectedModel);

            CategoriesModel result = categoriesService.createCategory(inputModel);

            assertNotNull(result);
            assertEquals("Venue", result.categoryName());
            mockedStatic.verify(() -> CategoriesMapper.toModel(savedEntity), times(1));
        }
    }

    @Test
    void testCreateCategory_throwsIfAlreadyExists() {
        CategoriesModel inputModel = new CategoriesModel(
                null,
                "UCAT-1",
                "Venue",
                null,
                null,
                List.of(),
                Set.of()
        );

        Categories existing = new Categories();
        existing.setCategoryName("Venue");

        when(categoriesRepository.findByCategoryNameIgnoreCase("Venue")).thenReturn(existing);
        assertThrows(ResourceAlreadyExistsException.class, () -> categoriesService.createCategory(inputModel));

        verify(categoriesRepository, never()).save(any());
    }

    @Test
    void testUpdateCategory_successfullyUpdatesCategory() {
        Long categoryId = 1L;

        CategoriesModel inputModel = new CategoriesModel(
                categoryId,
                "UCAT-1",
                "Updated Venue",
                null,
                null,
                List.of(),
                Set.of()
        );

        Categories existingEntity = new Categories();
        existingEntity.setCategoryId(categoryId);
        existingEntity.setCategoryName("Old Venue");

        Categories savedEntity = new Categories();
        savedEntity.setCategoryId(categoryId);
        savedEntity.setCategoryName("Updated Venue");

        CategoriesModel expectedModel = new CategoriesModel(
                categoryId,
                "UCAT-1",
                "Updated Venue",
                OffsetDateTime.now(),
                null,
                List.of(),
                Set.of()
        );

        when(categoriesRepository.findById(categoryId)).thenReturn(Optional.of(existingEntity));
        when(categoriesRepository.findByCategoryNameIgnoreCase("Updated Venue")).thenReturn(null);
        when(categoriesRepository.save(any(Categories.class))).thenReturn(savedEntity);

        try (MockedStatic<CategoriesMapper> mockedStatic = mockStatic(CategoriesMapper.class)) {
            mockedStatic.when(() -> CategoriesMapper.toModel(savedEntity)).thenReturn(expectedModel);

            CategoriesModel result = categoriesService.updateCategory(categoryId, inputModel);

            assertNotNull(result);
            assertEquals("Updated Venue", result.categoryName());
            mockedStatic.verify(() -> CategoriesMapper.toModel(savedEntity), times(1));
        }
    }

    @Test
    void testUpdateCategory_throwsIfNotFound() {
        Long categoryId = 404L;

        CategoriesModel inputModel = new CategoriesModel(
                categoryId,
                "UCAT-404",
                "Photography",
                null,
                null,
                List.of(),
                Set.of()
        );

        when(categoriesRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoriesService.updateCategory(categoryId, inputModel));

        verify(categoriesRepository, never()).save(any());
    }

    @Test
    void testUpdateCategory_throwsIfAlreadyExists() {
        Long categoryId = 1L;

        CategoriesModel inputModel = new CategoriesModel(
                categoryId,
                "UCAT-1",
                "Duplicate Name",
                null,
                null,
                List.of(),
                Set.of()
        );

        Categories existingEntity = new Categories();
        existingEntity.setCategoryId(categoryId);
        existingEntity.setCategoryName("Old Name");

        Categories existingDuplicate = new Categories();

        when(categoriesRepository.findById(categoryId)).thenReturn(Optional.of(existingEntity));
        when(categoriesRepository.findByCategoryNameIgnoreCase("Duplicate Name")).thenReturn(existingDuplicate);

        assertThrows(ResourceAlreadyExistsException.class, () -> categoriesService.updateCategory(categoryId, inputModel));

        verify(categoriesRepository, never()).save(any());
    }

    @Test
    void testUpdateCategory_throwsIfIdNull() {
        CategoriesModel inputModel = new CategoriesModel(
                null,
                "UCAT-null",
                "Something",
                null,
                null,
                List.of(),
                Set.of()
        );

        assertThrows(NullPointerException.class, () -> categoriesService.updateCategory(null, inputModel));
    }

    @Test
    void testDeleteCategory_successfullyDeletes() {
        Long categoryId = 1L;

        Categories category = new Categories();
        category.setCategoryId(categoryId);
        category.setCategoryName("Photography");
        when(categoriesRepository.findById(categoryId)).thenReturn(Optional.of(category));
        categoriesService.deleteCategory(categoryId);
        verify(categoriesRepository, times(1)).delete(category);
    }

    @Test
    void testDeleteCategory_throwsIfNotFound() {
        Long categoryId = 404L;
        when(categoriesRepository.findById(categoryId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoriesService.deleteCategory(categoryId));
        verify(categoriesRepository, never()).delete(any());
    }

}
