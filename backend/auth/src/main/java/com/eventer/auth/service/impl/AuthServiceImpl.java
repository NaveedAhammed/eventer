package com.eventer.auth.service.impl;

import com.eventer.auth.dto.*;
import com.eventer.auth.entity.AuthUser;
import com.eventer.auth.exception.InternalServiceException;
import com.eventer.auth.exception.InvalidCredentialsException;
import com.eventer.auth.exception.ResourceNotFoundException;
import com.eventer.auth.exception.UserAlreadyExistsException;
import com.eventer.auth.mapper.AuthMapper;
import com.eventer.auth.repository.AuthUserRepository;
import com.eventer.auth.service.AuthService;
import com.eventer.auth.service.JwtService;
import com.eventer.auth.service.client.UsersFeignClient;
import jakarta.transaction.Transactional;
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
     * @return an {@link AuthTokensDto} containing access and refresh tokens, and user details
     */
    @Transactional
    @Override
    public AuthTokensDto register(RegisterDto registerDto) {
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

        return getAuthTokensDto(savedAuthUser);
    }

    /**
     * Logs in or registers a user using OAuth2 provider information.
     *
     * @param oAuth2UserInfo the {@link OAuth2UserInfo} object containing profile details
     * @return an {@link AuthTokensDto} containing access and refresh tokens, and user details
     */
    @Transactional
    @Override
    public AuthTokensDto oAuth2(OAuth2UserInfo oAuth2UserInfo) {
        AuthUser authUser = authUserRepository.findByEmail(oAuth2UserInfo.getEmail()).orElse(null);

        if (authUser != null) {
            if (authUser.getAuthProvider() != oAuth2UserInfo.getAuthProvider()){
                throw new InternalServiceException("User already exists with auth provider " + authUser.getAuthProvider().name());
            }
        }

        authUser = AuthMapper.toAuthUser(oAuth2UserInfo);
        authUser = authUserRepository.save(authUser);

        CreateProfileDto createProfileDto = AuthMapper.mapToCreateProfileDto(oAuth2UserInfo);
        createProfileDto.setUserId(authUser.getAuthUserId().toString());

        usersFeignClient.createProfile(createProfileDto);

        return getAuthTokensDto(authUser);
    }

    /**
     * Logs in a user with the provided credentials.
     *
     * @param loginDto the {@link LoginDto} object containing login credentials
     * @return an {@link AuthTokensDto} containing access and refresh tokens, and user details
     */
    @Override
    public AuthTokensDto login(LoginDto loginDto) {
        AuthUser authUser = authUserRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail())
                .orElse(null);
        log.debug("AuthUser: {}", authUser);

        if (authUser == null) {
            throw new ResourceNotFoundException("AuthUser", "username or email", loginDto.getUsernameOrEmail());
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), authUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return getAuthTokensDto(authUser);
    }

    /**
     * Generates access and refresh tokens for the authenticated user.
     *
     * @param authUser the {@link AuthUser} object representing the authenticated user
     * @return a map containing access and refresh tokens
     */
    private AuthTokensDto getAuthTokensDto(AuthUser authUser) {
        String accessToken = jwtService.generateToken(authUser, false);
        String refreshToken = jwtService.generateToken(authUser, true);

        AuthTokensDto authTokensDto = AuthMapper.toAuthResponseDto(accessToken, refreshToken);
        log.debug("AuthTokensDto: {}", authTokensDto);

        return authTokensDto;
    }
}
