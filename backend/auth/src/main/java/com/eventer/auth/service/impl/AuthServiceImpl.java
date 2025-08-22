package com.eventer.auth.service.impl;

import com.eventer.auth.dto.AuthResponseDto;
import com.eventer.auth.dto.CreateProfileDto;
import com.eventer.auth.dto.LoginDto;
import com.eventer.auth.dto.RegisterDto;
import com.eventer.auth.entity.AuthUser;
import com.eventer.auth.exception.InvalidCredentialsException;
import com.eventer.auth.exception.ResourceNotFoundException;
import com.eventer.auth.exception.UserAlreadyExistsException;
import com.eventer.auth.mapper.AuthMapper;
import com.eventer.auth.repository.AuthUserRepository;
import com.eventer.auth.service.AuthService;
import com.eventer.auth.service.JwtService;
import com.eventer.auth.service.client.UsersFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final UsersFeignClient usersFeignClient;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

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

        AuthUser authUser = AuthMapper.toAuthUser(registerDto);
        authUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        AuthUser savedAuthUser = authUserRepository.save(authUser);
        log.debug("Saved AuthUser {}", savedAuthUser);

        CreateProfileDto createProfileDto = AuthMapper.mapToCreateProfileDto(registerDto);
        createProfileDto.setUserId(savedAuthUser.getAuthUserId().toString());

        usersFeignClient.createProfile(createProfileDto);
        String accessToken = jwtService.generateToken(authUser, false);
        String refreshToken = jwtService.generateToken(authUser, true);

        AuthResponseDto authResponseDto = AuthMapper.toAuthResponseDto(savedAuthUser, accessToken, refreshToken);
        log.debug("AuthResponseDto: {}", authResponseDto);

        return authResponseDto;
    }

    /**
     * Logs in a user with the provided credentials.
     *
     * @param loginDto the {@link LoginDto} object containing login credentials
     * @return an {@link AuthResponseDto} containing access and refresh tokens, and user details
     */
    @Override
    public AuthResponseDto login(LoginDto loginDto) {
        AuthUser authUser = authUserRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail())
                .orElse(null);
        log.debug("AuthUser: {}", authUser);

        if (authUser == null) {
            throw new ResourceNotFoundException("AuthUser", "username or email", loginDto.getUsernameOrEmail());
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), authUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(authUser, false);
        String refreshToken = jwtService.generateToken(authUser, true);

        return AuthMapper.toAuthResponseDto(authUser, accessToken, refreshToken);
    }
}
