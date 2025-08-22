package com.eventer.auth.mapper;

import com.eventer.auth.dto.AuthResponseDto;
import com.eventer.auth.dto.AuthUserDto;
import com.eventer.auth.dto.CreateProfileDto;
import com.eventer.auth.dto.RegisterDto;
import com.eventer.auth.entity.AuthUser;
import com.eventer.auth.entity.enums.AuthProvider;
import com.eventer.auth.entity.enums.Role;

public class AuthMapper {
    public static CreateProfileDto mapToCreateProfileDto(RegisterDto registerDto) {
        return CreateProfileDto.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .username(registerDto.getUsername())
                .role(Role.fromValue(registerDto.getRole()).name())
                .authProvider(AuthProvider.LOCAL.name())
                .build();
    }

    public static AuthUser toAuthUser(RegisterDto registerDto) {
        return AuthUser.builder()
                .email(registerDto.getEmail())
                .authProvider(AuthProvider.LOCAL)
                .role(Role.fromValue(registerDto.getRole()))
                .username(registerDto.getUsername())
                .build();
    }

    public static AuthUserDto toAuthUserDto(AuthUser authUser) {
        return AuthUserDto.builder()
                .authUserId(authUser.getAuthUserId().toString())
                .email(authUser.getEmail())
                .username(authUser.getUsername())
                .role(authUser.getRole().name())
                .authProvider(authUser.getAuthProvider().name())
                .build();
    }

    public static AuthResponseDto toAuthResponseDto(AuthUser authUser, String accessToken, String refreshToken) {
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authUser(toAuthUserDto(authUser))
                .build();
    }
}
