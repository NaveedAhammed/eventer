package com.eventer.userservice.exception;

public class UserUnAuthenticatedException extends RuntimeException {
    public UserUnAuthenticatedException(String message) {
        super(message);
    }
}
