package com.bwg.restapi;


import com.bwg.model.AuthModel;
import com.bwg.model.TagModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.TagService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TagModel>> getAllTags(@RequestParam(required = false)String search, Pageable pageable) {
        return ResponseEntity.ok(tagService.getAllTags(search,pageable));
    }

    @PermitAll
    @GetMapping(value = "/{tagId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TagModel> getUserById(@PathVariable(value = "tagId") final Long tagId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(tagService.getTagById(tagId, authModel));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PermitAll
    public ResponseEntity<TagModel> createTag(@RequestBody TagModel tagModel,@AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(tagService.createTag(tagModel,authModel));
    }

    @PostMapping(value = "/{tagId}/assign-to-service/{serviceId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TagModel> assignTagToService(@PathVariable Long tagId, @PathVariable Long serviceId) {
        return ResponseEntity.ok(tagService.assignTagToService(tagId,serviceId));
    }

    @PostMapping(value = "/{tagId}/assign-to-category/{categoryId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TagModel> assignTagToCategory(@PathVariable Long tagId, @PathVariable Long categoryId) {
        return ResponseEntity.ok(tagService.assignTagToCategory(tagId,categoryId));
    }

    @PermitAll
    @DeleteMapping(value = "/{tagId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable(value = "tagId") final Long tagId, @AuthPrincipal AuthModel authModel) {
        tagService.deleteTag(tagId, authModel);
        return ResponseEntity.noContent().build();

    }
}

