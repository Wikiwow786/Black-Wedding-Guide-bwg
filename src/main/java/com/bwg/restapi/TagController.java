package com.bwg.restapi;


import com.bwg.model.AuthModel;
import com.bwg.model.TagModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.TagService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TagModel>> getAllTags(@RequestParam(required = false)String search,@AuthPrincipal AuthModel authModel, Pageable pageable) {
        return ResponseEntity.ok(tagService.getAllTags(search,pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{tagId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TagModel> getUserById(@PathVariable(value = "tagId") final Long tagId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(tagService.getTagById(tagId, authModel));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TagModel> createTag(@RequestBody TagModel tagModel,@AuthPrincipal AuthModel authModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(tagModel,authModel));
    }

    @PutMapping(value = "/{tagId}/service/{serviceId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TagModel> assignTagToService(@PathVariable Long tagId, @PathVariable Long serviceId,@AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(tagService.assignTagToService(tagId,serviceId));
    }

    @PutMapping(value = "/{tagId}/category/{categoryId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TagModel> assignTagToCategory(@PathVariable Long tagId, @PathVariable Long categoryId,@AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(tagService.assignTagToCategory(tagId,categoryId));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/{tagId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable(value = "tagId") final Long tagId, @AuthPrincipal AuthModel authModel) {
        tagService.deleteTag(tagId, authModel);
        return ResponseEntity.noContent().build();

    }
}

