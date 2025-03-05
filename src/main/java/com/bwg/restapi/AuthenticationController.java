package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.UsersModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UsersService userService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersModel> update(@RequestBody UsersModel usersModel, @AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(new UsersModel(userService.createUser(usersModel,authModel)));
    }
}
