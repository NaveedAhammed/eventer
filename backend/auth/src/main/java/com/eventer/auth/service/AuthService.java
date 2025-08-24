package com.eventer.auth.service;

import com.eventer.auth.dto.*;

public interface AuthService {
    /**
     * Registers a new user with the provided details.
     *
     * @param registerDto the {@link RegisterDto} object containing registration details
     * @return an {@link AuthTokensDto} containing access and refresh tokens, and user details
     */
    AuthTokensDto register(RegisterDto registerDto);

    /**
     * Logs in or registers a user using OAuth2 provider information.
     *
     * @param oAuth2UserInfo the {@link OAuth2UserInfo} object containing profile details
     * @return an {@link AuthTokensDto} containing access and refresh tokens, and user details
     */
    AuthTokensDto oAuth2(OAuth2UserInfo oAuth2UserInfo);

    /**
     * Logs in a user with the provided credentials.
     *
     * @param loginDto the {@link LoginDto} object containing login credentials
     * @return an {@link AuthTokensDto} containing access and refresh tokens, and user details
     */
    AuthTokensDto login(LoginDto loginDto);

    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param refreshToken the refresh token
     * @return a new access token
     */
    String refresh(String refreshToken);
}
