package com.bwg.restapi;

import com.bwg.model.CategoriesModel;
import com.bwg.service.CategoriesService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @PermitAll
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoriesModel>> getAllCategories() {
        return ResponseEntity.ok(categoriesService.getAllCategories().stream().map(CategoriesModel::new).toList());
    }

    @PermitAll
    @GetMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesModel> getCategoriesById(@PathVariable(value = "categoryId") final Long categoryId) {
        return ResponseEntity.ok(new CategoriesModel(categoriesService.getCategoryById(categoryId)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesModel> createCategory(@RequestBody CategoriesModel categoriesModel) {
        return ResponseEntity.ok(new CategoriesModel(categoriesService.createCategory(categoriesModel)));
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesModel> updateCategory(@PathVariable(value = "categoryId") final Long categoryId,
                                                          @RequestBody CategoriesModel categoriesModel) {
        return ResponseEntity.ok(new CategoriesModel(categoriesService.updateCategory(categoryId, categoriesModel)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteCategory(@PathVariable(value = "categoryId") final Long categoryId) {
        categoriesService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }
}
