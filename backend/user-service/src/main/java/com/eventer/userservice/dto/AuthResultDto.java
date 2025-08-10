package com.eventer.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResultDto {
    private String refreshToken;
    private AuthResponseDto authResponseDto;
}
