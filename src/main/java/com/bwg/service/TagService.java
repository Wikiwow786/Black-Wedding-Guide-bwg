package com.bwg.service;

import com.bwg.domain.Tag;
import com.bwg.domain.Tag;
import com.bwg.model.AuthModel;
import com.bwg.model.TagModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {
    Page<TagModel> getAllTags(String search,Pageable pageable);

    TagModel getTagById(Long tagId, AuthModel authModel);

    TagModel createTag(TagModel tagModel, AuthModel authModel);

    TagModel assignTagToService(Long tagId,Long serviceId);

    TagModel assignTagToCategory(Long tagId,Long categoryId);

    TagModel updateTag(Long userId, TagModel tagModel);

    void deleteTag(Long tagId, AuthModel authModel);
}
