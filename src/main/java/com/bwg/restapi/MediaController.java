package com.bwg.restapi;

import com.bwg.model.MediaModel;
import com.bwg.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MediaModel>> getAllMedia() {
        return ResponseEntity.ok(mediaService.getAllMedia().stream().map(MediaModel::new).toList());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @GetMapping(value = "/{mediaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaModel> getMediaById(@PathVariable(value = "mediaId") final Long mediaId) {
        return ResponseEntity.ok(new MediaModel(mediaService.getMedia(mediaId)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaModel> createMedia(@RequestBody MediaModel mediaModel) {
        return ResponseEntity.ok(new MediaModel(mediaService.createMedia(mediaModel)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @DeleteMapping(value = "/{mediaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteMedia(@PathVariable(value = "mediaId") final Long mediaId) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.ok().build();
    }
}
