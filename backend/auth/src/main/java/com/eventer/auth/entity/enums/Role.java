package com.eventer.auth.entity.enums;

import java.util.stream.Stream;

public enum Role {
    USER,
    ORGANIZER,
    ADMIN;

    public static Role fromValue(String value){
        return Stream.of(values())
                .filter(role -> role.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + value));
    }
}
