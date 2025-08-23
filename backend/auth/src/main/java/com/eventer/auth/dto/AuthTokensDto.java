package com.eventer.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthTokensDto {
    private String accessToken;
    private String refreshToken;
}
