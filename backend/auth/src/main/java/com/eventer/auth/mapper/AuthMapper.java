package com.eventer.auth.mapper;

import com.eventer.auth.dto.*;
import com.eventer.auth.entity.AuthUser;
import com.eventer.auth.entity.enums.AuthProvider;
import com.eventer.auth.entity.enums.Role;

public class AuthMapper {
    public static CreateProfileDto mapToCreateProfileDto(RegisterDto registerDto) {
        return CreateProfileDto.builder()
                .email(registerDto.getEmail())
                .username(registerDto.getUsername())
                .role(Role.fromValue(registerDto.getRole()).name())
                .authProvider(AuthProvider.LOCAL.name())
                .build();
    }

    public static CreateProfileDto mapToCreateProfileDto(OAuth2UserInfo oAuth2UserInfo) {
        return CreateProfileDto.builder()
                .email(oAuth2UserInfo.getEmail())
                .username(oAuth2UserInfo.getUsername())
                .role(oAuth2UserInfo.getRole().name())
                .authProvider(oAuth2UserInfo.getAuthProvider().name())
                .profilePictureUrl(oAuth2UserInfo.getProfilePictureUrl())
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

    public static AuthUser toAuthUser(OAuth2UserInfo oAuth2UserInfo) {
        return AuthUser.builder()
                .email(oAuth2UserInfo.getEmail())
                .username(oAuth2UserInfo.getUsername())
                .authProvider(oAuth2UserInfo.getAuthProvider())
                .role(oAuth2UserInfo.getRole())
                .build();
    }

    public static AuthTokensDto toAuthResponseDto(String accessToken, String refreshToken) {
        return AuthTokensDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
