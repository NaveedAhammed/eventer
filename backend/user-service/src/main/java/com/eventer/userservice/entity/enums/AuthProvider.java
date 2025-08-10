package com.eventer.userservice.entity.enums;

import java.util.Arrays;

public enum AuthProvider {
    LOCAL("local"),
    GITHUB("github"),
    GOOGLE("google");

    private final String value;

    AuthProvider(String value) {
        this.value = value;
    }

    public static AuthProvider fromValue(String value) {
        return Arrays.stream(values())
                .filter(authProvider -> authProvider.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid auth provider: " + value));
    }
}
