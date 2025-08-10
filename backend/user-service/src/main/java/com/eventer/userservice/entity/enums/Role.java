package com.eventer.userservice.entity.enums;

import java.util.Arrays;

public enum Role {
    USER,
    ORGANIZER,
    ADMIN;

    public static Role fromValue(String value) {
        return Arrays.stream(values())
                .filter(role -> role.name().equalsIgnoreCase(value))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid role: " + value));
    }
}
