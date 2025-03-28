package com.bwg.model;

public record AuthModel(String authorization, String userId, String email, String correlationId,String role,String firstName,String lastName,String profilePicUrl) {
    @Override
    public String correlationId() {
        return correlationId;
    }

    @Override
    public String userId() {
        return userId;
    }

    @Override
    public String toString() {
        return "AuthModel[userId=" + userId + ", email=" + email + " correlationId=" + correlationId + "]";
    }
}
