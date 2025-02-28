package com.bwg.model;

public record AuthModel(String authorization, String userId, String email, String correlationId) {
}
