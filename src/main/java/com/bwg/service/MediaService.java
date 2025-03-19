package com.bwg.service;

import com.bwg.domain.Media;
import com.bwg.domain.Messages;
import com.bwg.model.MediaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MediaService {
    Page<Media> getAllMedia(String search, Long entityId, Pageable pageable);

    Media getMedia(Long mediaId);

    Media createMedia(MediaModel mediaModel);

    void deleteMedia(Long mediaId);
}
