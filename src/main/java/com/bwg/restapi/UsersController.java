package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.UsersModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UsersModel>> getAllUsers(@RequestParam(required = false)String search,@RequestParam(required = false)Long userId,@RequestParam(required = false)Long vendorId,@AuthPrincipal AuthModel authModel, Pageable pageable) {
        return ResponseEntity.ok(usersService.getAllUsers(search,userId,vendorId,pageable).map(UsersModel::new));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersModel> getUserById(@PathVariable(value = "userId") final Long userId, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new UsersModel(usersService.getUserById(userId, authModel)));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/currentUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersModel> fetchCurrentUser(@AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new UsersModel(usersService.getCurrentUser(authModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @PutMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersModel> update(@PathVariable(value = "userId") final Long userId,
                                             @RequestBody UsersModel usersModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new UsersModel(usersService.updateUser(userId, usersModel)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @DeleteMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable(value = "userId") final Long userId, @AuthPrincipal AuthModel authModel) {
        usersService.deleteUser(userId, authModel);
        return ResponseEntity.noContent().build();

    }

}
