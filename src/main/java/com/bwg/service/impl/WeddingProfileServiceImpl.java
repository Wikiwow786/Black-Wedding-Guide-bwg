package com.bwg.service.impl;

import com.bwg.domain.Users;
import com.bwg.domain.WeddingProfile;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.WeddingProfileModel;
import com.bwg.repository.UsersRepository;
import com.bwg.repository.WeddingProfileRepository;
import com.bwg.service.WeddingProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class WeddingProfileServiceImpl implements WeddingProfileService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private WeddingProfileRepository weddingProfileRepository;

    public WeddingProfileModel getWeddingProfile(Long userId) {
        Users users = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        WeddingProfile profile = weddingProfileRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    WeddingProfile wp = new WeddingProfile();
                    wp.setUser(users);
                    wp.setCreatedAt(OffsetDateTime.now());
                    return weddingProfileRepository.save(wp);
                });
        return new WeddingProfileModel(profile);
    }

    public WeddingProfileModel updateWeddingProfile(Long userId, WeddingProfileModel model) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        WeddingProfile profile = weddingProfileRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    WeddingProfile wp = new WeddingProfile();
                    wp.setUser(user);
                    wp.setCreatedAt(OffsetDateTime.now());
                    return wp;
                });
        BeanUtils.copyProperties(model, profile, "weddingProfileId", "user", "uWeddingId", "createdAt", "updatedAt");
        profile.setUpdatedAt(OffsetDateTime.now());

        return new WeddingProfileModel(weddingProfileRepository.save(profile));
    }
}
