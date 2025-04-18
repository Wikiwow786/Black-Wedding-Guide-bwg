package com.bwg.service;

import com.bwg.model.WeddingProfileModel;

public interface WeddingProfileService {
    public WeddingProfileModel getWeddingProfile(Long userId) ;
    public WeddingProfileModel updateWeddingProfile(Long userId, WeddingProfileModel model);
}
