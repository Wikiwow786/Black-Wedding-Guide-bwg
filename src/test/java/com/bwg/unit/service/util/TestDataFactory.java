package com.bwg.unit.service.util;

import com.bwg.model.AuthModel;

public class TestDataFactory {
    public static AuthModel buildAuthModel(String id, String role) {
        return new AuthModel("Bearer abc", id, id + "@example.com", "corr-id",
                role, "Test", "User", "https://img.com/pic.jpg");
    }
}
