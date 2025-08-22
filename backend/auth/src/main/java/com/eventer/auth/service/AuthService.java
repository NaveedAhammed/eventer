package com.eventer.auth.service;

import com.eventer.auth.dto.AuthResponseDto;
import com.eventer.auth.dto.LoginDto;
import com.eventer.auth.dto.RegisterDto;

public interface AuthService {
    /**
     * Registers a new user with the provided details.
     *
     * @param registerDto the {@link RegisterDto} object containing registration details
     * @return an {@link AuthResponseDto} containing access and refresh tokens, and user details
     */
    AuthResponseDto register(RegisterDto registerDto);

    /**
     * Logs in a user with the provided credentials.
     *
     * @param loginDto the {@link LoginDto} object containing login credentials
     * @return an {@link AuthResponseDto} containing access and refresh tokens, and user details
     */
    AuthResponseDto login(LoginDto loginDto);
}
