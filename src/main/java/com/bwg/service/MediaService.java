package com.bwg.service;

import com.bwg.domain.Media;
import com.bwg.model.MediaModel;

import java.util.List;

public interface MediaService {
    List<Media> getAllMedia();

    Media getMedia(Long mediaId);

    Media createMedia(MediaModel mediaModel);

    void deleteMedia(Long mediaId);
}
