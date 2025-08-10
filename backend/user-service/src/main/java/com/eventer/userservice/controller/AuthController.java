package com.eventer.userservice.controller;

import com.eventer.userservice.dto.AuthResponseDto;
import com.eventer.userservice.dto.AuthResultDto;
import com.eventer.userservice.dto.LoginRequestDto;
import com.eventer.userservice.dto.RegisterRequestDto;
import com.eventer.userservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.eventer.userservice.constants.Constants.REFRESH_TOKEN_PARAM;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto requestDto, HttpServletResponse response) {
        log.info("Register request: {}", requestDto);
        AuthResultDto authResultDto = authService.register(requestDto);
        setRefreshTokenCookie(response, authResultDto.getRefreshToken());
        return new ResponseEntity<>(authResultDto.getAuthResponseDto(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        log.info("Login request: {}", requestDto);
        AuthResultDto authResultDto = authService.login(requestDto);
        setRefreshTokenCookie(response, authResultDto.getRefreshToken());
        return new ResponseEntity<>(authResultDto.getAuthResponseDto(), HttpStatus.OK);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken){
        Cookie cookie = new Cookie(REFRESH_TOKEN_PARAM, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");

        response.addCookie(cookie);

        log.info("Added refresh_token cookie");
    }
}
