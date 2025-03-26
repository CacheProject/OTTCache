package org.example.cacheproject.domain.user.enums;

import org.example.cacheproject.common.exception.BadRequestException;

import java.util.Arrays;

public enum UserRole {
    USER,
    OWNER,
    ADMIN;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("유효하지 않은 UserRole"));
    }

    public boolean canHaveStore() {
        return this == ADMIN || this == OWNER;
    }
}
