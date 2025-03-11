package com.bwg.model;

public record AuthModel(String authorization, String userId, String email, String correlationId) {
    @Override
    public String correlationId() {
        return correlationId;
    }

    @Override
    public String userId() {
        return userId;
    }
}
