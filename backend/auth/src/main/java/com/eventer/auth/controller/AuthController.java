package com.eventer.auth.controller;

import com.eventer.auth.dto.AuthResponseDto;
import com.eventer.auth.dto.LoginDto;
import com.eventer.auth.dto.RegisterDto;
import com.eventer.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto) {
        log.debug("Registering user with details: {}", registerDto);
        AuthResponseDto response = authService.register(registerDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        log.debug("Logging in user with credentials: {}", loginDto);
        AuthResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
}
