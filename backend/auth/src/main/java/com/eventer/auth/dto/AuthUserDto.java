package com.eventer.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthUserDto {
    private String authUserId;
    private String username;
    private String email;
    private String authProvider;
    private String role;
}
