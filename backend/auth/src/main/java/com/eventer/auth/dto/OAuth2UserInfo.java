package com.eventer.auth.dto;

import com.eventer.auth.entity.enums.AuthProvider;
import com.eventer.auth.entity.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth2UserInfo {
    private String email;
    private String username;
    private String profilePictureUrl;
    private AuthProvider authProvider;
    private Role role;
}
