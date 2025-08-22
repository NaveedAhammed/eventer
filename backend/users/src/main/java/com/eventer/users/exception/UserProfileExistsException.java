package com.eventer.users.exception;

public class UserProfileExistsException extends RuntimeException {
    public UserProfileExistsException(String message) {
        super(message);
    }
}
