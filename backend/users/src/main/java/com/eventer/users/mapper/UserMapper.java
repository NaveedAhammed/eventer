package com.eventer.users.mapper;

import com.eventer.users.dto.CreateProfileDto;
import com.eventer.users.dto.UserDto;
import com.eventer.users.entity.User;

import java.util.UUID;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .authProvider(user.getAuthProvider())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }

    public static User toUser(CreateProfileDto createProfileDto) {
        UUID userId = UUID.fromString(createProfileDto.getUserId());
        return User.builder()
                .userId(userId)
                .email(createProfileDto.getEmail())
                .username(createProfileDto.getUsername())
                .role(createProfileDto.getRole())
                .authProvider(createProfileDto.getAuthProvider())
                .profilePictureUrl(createProfileDto.getProfilePictureUrl())
                .build();
    }
}
