package com.bwg.restapi;

import com.bwg.domain.Media;
import com.bwg.model.AuthModel;
import com.bwg.model.MediaModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<MediaModel>> getAllMedia(@RequestParam(required = false)String search,@RequestParam(required = false)Long entityId,@AuthPrincipal AuthModel authModel, Pageable pageable) {
        return ResponseEntity.ok(mediaService.getAllMedia(search,entityId,pageable).map(MediaModel::new));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/{mediaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaModel> getMediaById(@PathVariable(value = "mediaId") final Long mediaId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new MediaModel(mediaService.getMedia(mediaId)));
    }

    @GetMapping("/{mediaId}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long mediaId,@AuthPrincipal AuthModel authModel) {
        return mediaService.downloadFile(mediaId);
    }

    @GetMapping(value = "/entity/{entityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MediaModel>> getEntity(@PathVariable(required = false) Long entityId) {
        return ResponseEntity.ok(mediaService.getByEntity(entityId));
    }

    @GetMapping(value = "/url/{mediaId}")
    public ResponseEntity<Object> getUrl(@PathVariable Long mediaId) {
        return ResponseEntity.ok(mediaService.getUrl(mediaId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<?> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Media.EntityType entityType, @AuthPrincipal AuthModel authModel) {

        try {
            Media media = mediaService.uploadMedia(entityId, title, entityType, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(media);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("File upload failed: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @DeleteMapping(value = "/{mediaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteMedia(@PathVariable(value = "mediaId") final Long mediaId, @AuthPrincipal AuthModel authModel) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.noContent().build();
    }
}
