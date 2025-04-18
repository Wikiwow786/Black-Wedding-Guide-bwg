package com.bwg.restapi;

import com.bwg.model.AuthModel;
import com.bwg.model.WeddingProfileModel;
import com.bwg.resolver.AuthPrincipal;
import com.bwg.service.WeddingProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile/wedding")
public class WeddingProfileController {

    @Autowired
    private  WeddingProfileService weddingProfileService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WeddingProfileModel> getWeddingProfile(@AuthPrincipal AuthModel authModel) {
        return ResponseEntity.ok(weddingProfileService.getWeddingProfile(Long.parseLong(authModel.userId())));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WeddingProfileModel> updateWeddingProfile(@AuthPrincipal AuthModel authModel,
                                                                    @RequestBody WeddingProfileModel model) {
       return ResponseEntity.ok(weddingProfileService.updateWeddingProfile(Long.parseLong(authModel.userId()), model));

    }
}

