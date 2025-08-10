package com.eventer.userservice.mapper;

import com.eventer.userservice.dto.RegisterRequestDto;
import com.eventer.userservice.dto.UserDto;
import com.eventer.userservice.entity.User;
import com.eventer.userservice.entity.enums.AuthProvider;
import com.eventer.userservice.entity.enums.Role;

public class UserMapper {
    public static User toUser(RegisterRequestDto requestDto) {
        return User.builder()
                .email(requestDto.getEmail())
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .role(Role.fromValue(requestDto.getRole()))
                .authProvider(AuthProvider.LOCAL)
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .authProvider(user.getAuthProvider().name())
                .profilePictureUrl(user.getProfilePictureUrl())
                .verified(user.isVerified())
                .build();
    }
}
