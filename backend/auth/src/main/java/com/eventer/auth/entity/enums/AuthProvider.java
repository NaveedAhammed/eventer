package com.eventer.auth.entity.enums;

public enum AuthProvider {
    LOCAL,
    GOOGLE,
    GITHUB;

    public static AuthProvider fromValue(String value) {
        for (AuthProvider provider : values()) {
            if (provider.name().equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown auth provider: " + value);
    }
}
