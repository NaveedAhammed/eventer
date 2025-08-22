package com.eventer.auth.service.impl;

import com.eventer.auth.dto.AuthResponseDto;
import com.eventer.auth.dto.LoginDto;
import com.eventer.auth.dto.RegisterDto;
import com.eventer.auth.exception.UserAlreadyExistsException;
import com.eventer.auth.repository.AuthUserRepository;
import com.eventer.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;

    /**
     * Registers a new user with the provided details.
     *
     * @param registerDto the {@link RegisterDto} object containing registration details
     * @return an {@link AuthResponseDto} containing access and refresh tokens, and user details
     */
    @Override
    public AuthResponseDto register(RegisterDto registerDto) {
        if (authUserRepository.existsByEmail(registerDto.getEmail())) {
            log.warn("User with email {} already exists", registerDto.getEmail());
            throw new UserAlreadyExistsException("User with this email already exists");
        }


        return null;
    }

    /**
     * Logs in a user with the provided credentials.
     *
     * @param loginDto the {@link LoginDto} object containing login credentials
     * @return an {@link AuthResponseDto} containing access and refresh tokens, and user details
     */
    @Override
    public AuthResponseDto login(LoginDto loginDto) {
        return null;
    }
}
