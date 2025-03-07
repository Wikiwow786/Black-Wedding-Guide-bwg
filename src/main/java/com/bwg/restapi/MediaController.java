package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.MediaModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.MediaService;
import com.bwg.util.CorrelationIdHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<MediaModel>> getAllMedia(@AuthPrincipal AuthModel authModel, Pageable pageable) {
        return ResponseEntity.ok(mediaService.getAllMedia(pageable).map(MediaModel::new));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @GetMapping(value = "/{mediaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaModel> getMediaById(@PathVariable(value = "mediaId") final Long mediaId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new MediaModel(mediaService.getMedia(mediaId)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaModel> createMedia(@RequestBody MediaModel mediaModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new MediaModel(mediaService.createMedia(mediaModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @DeleteMapping(value = "/{mediaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteMedia(@PathVariable(value = "mediaId") final Long mediaId, @AuthPrincipal AuthModel authModel) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.ok().build();
    }
}
