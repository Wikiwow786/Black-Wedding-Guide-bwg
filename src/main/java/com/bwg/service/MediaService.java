package com.bwg.service;

import com.bwg.domain.Media;
import com.bwg.model.MediaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface MediaService {
    Page<Media> getAllMedia(String search, Long entityId, Pageable pageable);

    List<MediaModel> getByEntity(Long entityId);

    Media getMedia(Long mediaId);

    String getUrl(Long mediaId);

    Media createMedia(MediaModel mediaModel);

    Media uploadMedia(Long entityId, Media.EntityType entityType, MultipartFile file) throws IOException;

    ResponseEntity<byte[]> downloadFile(Long mediaId);

    void deleteMedia(Long mediaId);
}
