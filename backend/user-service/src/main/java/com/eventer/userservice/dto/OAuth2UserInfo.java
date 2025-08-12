package com.eventer.userservice.dto;

import com.eventer.userservice.entity.enums.AuthProvider;
import com.eventer.userservice.entity.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth2UserInfo {
    private String email;
    private String firstName;
    private String profilePictureUrl;
    private AuthProvider authProvider;
    private Role role;
}
