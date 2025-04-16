package com.bwg.unit.service.impl;

import com.bwg.domain.Categories;
import com.bwg.domain.Services;
import com.bwg.domain.Tag;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.TagModel;
import com.bwg.repository.CategoriesRepository;
import com.bwg.repository.ServicesRepository;
import com.bwg.repository.TagRepository;
import com.bwg.service.impl.TagServiceImpl;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.bwg.unit.service.util.TestDataFactory.buildAuthModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TagServiceImplTest {
    @Mock
    private TagRepository tagRepository;
    @Mock
    private ServicesRepository servicesRepository;
    @Mock
    private CategoriesRepository categoriesRepository;
    @InjectMocks
    private TagServiceImpl tagService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTags_withSearchFilter() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String search = "wedding";

        Tag tag = new Tag();
        tag.setTagId(1L);
        tag.setName("Wedding Planning");

        Page<Tag> tagPage = new PageImpl<>(List.of(tag));
        when(tagRepository.findAll(any(BooleanBuilder.class), eq(pageable))).thenReturn(tagPage);
        Page<TagModel> result = tagService.getAllTags(search, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Wedding Planning", result.getContent().get(0).getName());
    }

    @Test
    void testGetAllTags_withoutSearchFilter() {
        Pageable pageable = PageRequest.of(0, 5);

        Tag tag = new Tag();
        tag.setTagId(2L);
        tag.setName("Decor");

        Page<Tag> tagPage = new PageImpl<>(List.of(tag));
        when(tagRepository.findAll(any(BooleanBuilder.class), eq(pageable))).thenReturn(tagPage);
        Page<TagModel> result = tagService.getAllTags(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Decor", result.getContent().get(0).getName());
    }

    @Test
    void testGetTagById_returnsTagModel() {
        Long tagId = 1L;
        AuthModel authModel = buildAuthModel("1", "ROLE_ADMIN");

        Tag tag = new Tag();
        tag.setTagId(tagId);
        tag.setName("Photography");

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        TagModel result = tagService.getTagById(tagId, authModel);

        assertNotNull(result);
        assertEquals("Photography", result.getName());
    }

    @Test
    void testCreateTag_successfullyCreatesTag() {

        AuthModel authModel = buildAuthModel("123", "ROLE_ADMIN");

        Tag savedEntity = new Tag();
        savedEntity.setTagId(1L);
        savedEntity.setName("Floral");

        TagModel tagModel = new TagModel(savedEntity);

        when(tagRepository.save(any(Tag.class))).thenReturn(savedEntity);

        // Act
        TagModel result = tagService.createTag(tagModel, authModel);

        // Assert
        assertNotNull(result);
        assertEquals("Floral", result.getName());
        assertEquals(1L, result.getTagId());

        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void testAssignTagToService_successfullyAssignsTag() {
        Long tagId = 1L;
        Long serviceId = 10L;

        Tag tag = new Tag();
        tag.setTagId(tagId);
        tag.setName("Photography");

        Services service = new Services();
        service.setServiceId(serviceId);
        service.setTags(new HashSet<>());

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(servicesRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(servicesRepository.save(service)).thenReturn(service);

        TagModel result = tagService.assignTagToService(tagId, serviceId);

        assertNotNull(result);
        assertEquals("Photography", result.getName());
        assertTrue(service.getTags().contains(tag));

        verify(servicesRepository).save(service);
    }

    @Test
    void testAssignTagToService_throwsIfTagNotFound() {
        Long tagId = 1L;
        Long serviceId = 10L;

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.assignTagToService(tagId, serviceId));

        verify(servicesRepository, never()).findById(any());
        verify(servicesRepository, never()).save(any());
    }

    @Test
    void testAssignTagToService_throwsIfServiceNotFound() {
        Long tagId = 1L;
        Long serviceId = 10L;

        Tag tag = new Tag();
        tag.setTagId(tagId);

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(servicesRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.assignTagToService(tagId, serviceId));

        verify(servicesRepository, never()).save(any());
    }

    @Test
    void testAssignTagToService_throwsIfIdsNull() {
        assertThrows(NullPointerException.class, () -> tagService.assignTagToService(null, 1L));
        assertThrows(NullPointerException.class, () -> tagService.assignTagToService(1L, null));
    }


    @Test
    void testAssignTagToCategory_successfullyAssignsTag() {
        Long tagId = 1L;
        Long categoryId = 5L;

        Tag tag = new Tag();
        tag.setTagId(tagId);
        tag.setName("Decor");

        Categories category = new Categories();
        category.setCategoryId(categoryId);
        category.setTags(new HashSet<>());

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(categoriesRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoriesRepository.save(category)).thenReturn(category);

        TagModel result = tagService.assignTagToCategory(tagId, categoryId);

        assertNotNull(result);
        assertEquals("Decor", result.getName());
        assertTrue(category.getTags().contains(tag));

        verify(categoriesRepository).save(category);
    }


    @Test
    void testAssignTagToCategory_throwsIfTagNotFound() {
        Long tagId = 1L;
        Long categoryId = 5L;

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.assignTagToCategory(tagId, categoryId));

        verify(categoriesRepository, never()).findById(any());
        verify(categoriesRepository, never()).save(any());
    }

    @Test
    void testAssignTagToCategory_throwsIfCategoryNotFound() {
        Long tagId = 1L;
        Long categoryId = 5L;

        Tag tag = new Tag();
        tag.setTagId(tagId);

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(categoriesRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.assignTagToCategory(tagId, categoryId));

        verify(categoriesRepository, never()).save(any());
    }

    @Test
    void testAssignTagToCategory_throwsIfIdsAreNull() {
        assertThrows(NullPointerException.class, () -> tagService.assignTagToCategory(null, 1L));
        assertThrows(NullPointerException.class, () -> tagService.assignTagToCategory(1L, null));
    }

    @Test
    void testDeleteTag_successfullyDeletes() {
        Long tagId = 1L;
        AuthModel authModel = buildAuthModel("123", "ROLE_ADMIN");

        Tag tag = new Tag();
        tag.setTagId(tagId);

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        tagService.deleteTag(tagId, authModel);

        verify(tagRepository).findById(tagId);
        verify(tagRepository).delete(tag);
    }

    @Test
    void testDeleteTag_throwsIfTagNotFound() {
        Long tagId = 404L;
        AuthModel authModel = buildAuthModel("123", "ROLE_ADMIN");

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.deleteTag(tagId, authModel));

        verify(tagRepository).findById(tagId);
        verify(tagRepository, never()).delete(any());
    }




}
