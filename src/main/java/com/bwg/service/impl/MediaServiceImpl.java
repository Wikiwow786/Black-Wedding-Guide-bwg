package com.bwg.service.impl;

import com.bwg.domain.Media;
import com.bwg.domain.QMedia;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.MediaModel;
import com.bwg.repository.MediaRepository;
import com.bwg.service.MediaService;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Override
    public Page<Media> getAllMedia(String search,Long entityId,Pageable pageable) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching All Media", this);
        BooleanBuilder filter = new BooleanBuilder();
        if(StringUtils.isNotBlank(search)){
            filter.and(QMedia.media.title.containsIgnoreCase(search));
        }
        if(entityId != null){
            filter.and(QMedia.media.entityId.eq(entityId));
        }
        return mediaRepository.findAll(filter,pageable);
    }

    @Override
    public Media getMedia(Long mediaId) {
        info(LOG_SERVICE_OR_REPOSITORY, "Fetching Media by Id {0}", mediaId);
        return mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Override
    public Media createMedia(MediaModel mediaModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Media..."), this);

        Media media = new Media();

        BeanUtils.copyProperties(mediaModel, media);

        media.setCreatedAt(OffsetDateTime.now());
        return mediaRepository.save(media);
    }

    @Override
    public void deleteMedia(Long mediaId) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Delete Media information for Media Id {0} ", mediaId), this);
        mediaRepository.deleteById(mediaId);
    }
}
