package com.bwg.restapi;

import com.bwg.domain.Bookings;
import com.bwg.model.AuthModel;
import com.bwg.model.BookmarkModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {
    @Autowired
    private BookmarkService bookmarkService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<BookmarkModel>> getAllBookmarks(@RequestParam(required = false) String search,@AuthPrincipal AuthModel authModel, Pageable pageable) {
        return ResponseEntity.ok(bookmarkService.getAllBookmarks(search, authModel, pageable));

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/user/{userId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookmarkModel>> getByUser(@PathVariable(value = "userId") Long userId,@AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(bookmarkService.getUserBookmarks(userId,authModel));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookmarkModel> createBookmark(@RequestBody BookmarkModel bookmarkModel,@AuthPrincipal AuthModel authModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookmarkService.createBookmark(bookmarkModel,authModel));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteBookmark(@PathVariable(value = "id") Long id,@AuthPrincipal AuthModel authModel) {
        bookmarkService.deleteBookmark(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/user/{userId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteUserBookmarks(@PathVariable Long userId,@AuthPrincipal AuthModel authModel) {
        bookmarkService.deleteUserBookmarks(userId,authModel);
        return ResponseEntity.noContent().build();
    }
}