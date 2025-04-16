package com.bwg.enums;

import com.bwg.exception.UnauthorizedException;

public enum UserRole {
    ROLE_ADMIN,
    ROLE_COUPLE,
    ROLE_VENDOR,
    ROLE_OWNER;

    public static UserRole fromString(String role) {
        try {
            return UserRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Unauthorized Role: " + role);
        }
    }
}
