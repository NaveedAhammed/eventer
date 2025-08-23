package com.eventer.auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class CreateProfileDto {
    private String userId;
    private String email;
    private String username;
    private String role;
    private String authProvider;
    private String profilePictureUrl;
}
