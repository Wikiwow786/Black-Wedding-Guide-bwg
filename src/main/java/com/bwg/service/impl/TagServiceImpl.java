package com.bwg.service.impl;

import com.bwg.domain.Categories;
import com.bwg.domain.QTag;
import com.bwg.domain.Services;
import com.bwg.domain.Tag;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.TagModel;
import com.bwg.repository.CategoriesRepository;
import com.bwg.repository.ServicesRepository;
import com.bwg.repository.TagRepository;
import com.bwg.service.TagService;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;
@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ServicesRepository serviceRepository;

    @Autowired
    private CategoriesRepository categoryRepository;

    @Override
    public Page<TagModel> getAllTags(String search, Pageable pageable) {
        BooleanBuilder filter = new BooleanBuilder();
        if(StringUtils.isNotBlank(search)){
            filter.and(QTag.tag.name.containsIgnoreCase(search));
        }
        return tagRepository.findAll(filter,pageable).map(TagModel::new);
    }

    @Override
    public TagModel getTagById(Long tagId, AuthModel authModel) {
        return new TagModel(tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found")));
    }

    @Override
    public TagModel createTag(TagModel tagModel, AuthModel authModel) {
        return new TagModel(tagRepository.save(assemble(tagModel,authModel)));
    }

    @Override
    public TagModel assignTagToService(Long tagId, Long serviceId) {
        Objects.requireNonNull(serviceId, "Service ID cannot be null");
        Objects.requireNonNull(tagId,"Tag ID cannot be null");

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with ID: " + tagId));

        Services service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));
        info(LOG_SERVICE_OR_REPOSITORY, format("Adding tag for service", serviceId), this);
        service.getTags().add(tag);
        serviceRepository.save(service);
        return new TagModel(tag);
    }

    @Override
    public TagModel assignTagToCategory(Long tagId, Long categoryId) {
        Objects.requireNonNull(categoryId, "Service ID cannot be null");
        Objects.requireNonNull(tagId,"Tag ID cannot be null");
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with ID: " + tagId));

        Categories category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + categoryId));
        info(LOG_SERVICE_OR_REPOSITORY, format("Adding tag for category", categoryId), this);
        category.getTags().add(tag);
        categoryRepository.save(category);
        return new TagModel(tag);
    }

    @Override
    public TagModel updateTag(Long userId, TagModel tagModel) {
        return null;
    }

    @Override
    public void deleteTag(Long tagId, AuthModel authModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Delete tag for tagId {0} ", tagId), this);
      Tag tag =  tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        tagRepository.delete(tag);
    }

    private Tag assemble(TagModel tagModel,AuthModel authModel){
        Tag tag = new Tag();
       tag.setName(tagModel.getName());
       tag.setStatus(tagModel.getStatus());
       tag.setCreatedAt(OffsetDateTime.now());
       return tag;
    }
}
