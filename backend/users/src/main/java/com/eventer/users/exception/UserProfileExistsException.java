package com.eventer.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserProfileExistsException extends RuntimeException {
    public UserProfileExistsException(String message) {
        super(message);
    }
}
