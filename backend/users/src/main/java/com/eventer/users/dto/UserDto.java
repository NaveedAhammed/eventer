package com.eventer.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String authProvider;
    private String role;
    private String profilePictureUrl;
}
