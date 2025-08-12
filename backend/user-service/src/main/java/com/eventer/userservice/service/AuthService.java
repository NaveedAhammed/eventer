package com.eventer.userservice.service;

import com.eventer.userservice.dto.AuthResultDto;
import com.eventer.userservice.dto.LoginRequestDto;
import com.eventer.userservice.dto.RegisterRequestDto;

public interface AuthService {
    /**
     *
     * @param requestDto RegisterRequestDto Object
     * @return AuthResultDto Object which consist of accessToken, refreshToken and UserDto
     */
    AuthResultDto register(RegisterRequestDto requestDto);

    /**
     *
     * @param requestDto LoginRequestDto Object
     * @return AuthResultDto Object which consist of accessToken, refreshToken and UserDto
     */
    AuthResultDto login(LoginRequestDto requestDto);

    /**
     *
     * @param refreshToken Refresh Token
     * @return New Access Token
     */
    String refresh(String refreshToken);
}
