package com.eventer.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private boolean verified;
    private String role;
    private String authProvider;
}
