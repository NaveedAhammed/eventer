package com.eventer.auth.controller;

import com.eventer.auth.dto.AuthTokensDto;
import com.eventer.auth.dto.LoginDto;
import com.eventer.auth.dto.RegisterDto;
import com.eventer.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.eventer.auth.constants.AuthConstants.ACCESS_TOKEN_PARAM;
import static com.eventer.auth.constants.AuthConstants.REFRESH_TOKEN_PARAM;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterDto registerDto, HttpServletResponse response) {
        log.debug("Registering user with details: {}", registerDto);
        AuthTokensDto authTokensDto = authService.register(registerDto);

        setRefreshTokenCookie(response, authTokensDto.getRefreshToken());

        return ResponseEntity.ok(Map.of(ACCESS_TOKEN_PARAM, authTokensDto.getAccessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        log.debug("Logging in user with credentials: {}", loginDto);
        AuthTokensDto authTokensDto = authService.login(loginDto);

        setRefreshTokenCookie(response, authTokensDto.getRefreshToken());

        return ResponseEntity.ok(Map.of(ACCESS_TOKEN_PARAM, authTokensDto.getAccessToken()));
    }

    @GetMapping("/refresh-access-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@CookieValue(REFRESH_TOKEN_PARAM) String refreshToken) {
        String accessToken = authService.refresh(refreshToken);
        return ResponseEntity.ok(Map.of(ACCESS_TOKEN_PARAM, accessToken));
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken){
        Cookie cookie = new Cookie(REFRESH_TOKEN_PARAM, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
        log.debug("Added refresh_token cookie");
    }
}
