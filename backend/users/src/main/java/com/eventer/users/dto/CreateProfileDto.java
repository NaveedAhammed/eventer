package com.eventer.users.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateProfileDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String role;
    private String authProvider;
    private String profilePictureUrl;
}
