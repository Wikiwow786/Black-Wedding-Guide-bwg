package com.bwg.util;

import com.bwg.enums.UserRole;
import com.bwg.exception.ForbiddenException;
import com.bwg.model.AuthModel;

public class SecurityUtils {
    public static void checkOwnerOrAdmin(String targetUserId, AuthModel authModel) {
        boolean isOwner = targetUserId.equals(authModel.userId());
        boolean isAdmin = UserRole.ROLE_ADMIN.name().equals(authModel.role());

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("Access denied. Only owner or admin can perform this action.");
        }
    }

    public static void checkOwnerOrVendor(String targetVendorId, AuthModel authModel) {
        boolean isOwner = targetVendorId.equals(authModel.userId());
        boolean isAdmin = UserRole.ROLE_VENDOR.name().equals(authModel.role());

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("Access denied. Only owner or vendor can perform this action.");
        }
    }
}







