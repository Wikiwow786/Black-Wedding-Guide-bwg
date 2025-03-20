package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.CategoriesModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.CategoriesService;
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
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @PermitAll
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CategoriesModel>> getAllCategories(@RequestParam(required = false) String search,@RequestParam(required = false) String tagName,@AuthPrincipal AuthModel authModel, Pageable pageable) {
        return ResponseEntity.ok(categoriesService.getAllCategories(search,tagName,pageable));
    }

    @PermitAll
    @GetMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesModel> getCategoriesById(@PathVariable(value = "categoryId") final Long categoryId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(categoriesService.getCategoryById(categoryId));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesModel> createCategory(@RequestBody CategoriesModel categoriesModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriesService.createCategory(categoriesModel));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesModel> updateCategory(@PathVariable(value = "categoryId") final Long categoryId,
                                                          @RequestBody CategoriesModel categoriesModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(categoriesService.updateCategory(categoryId, categoriesModel));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteCategory(@PathVariable(value = "categoryId") final Long categoryId, @AuthPrincipal AuthModel authModel) {
        categoriesService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
