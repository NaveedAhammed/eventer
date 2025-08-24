package com.eventer.gatewayserver.filters;

import com.eventer.gatewayserver.dto.ErrorResponseDto;
import com.eventer.gatewayserver.exception.InvalidAccessTokenException;
import com.eventer.gatewayserver.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/register",
            "/api/v1/login",
            "/api/v1/refresh-access-token",
            "/oauth2/authorization/google",
            "/oauth2/authorization/github",
            "/login/oauth2/code/google",
            "/login/oauth2/code/github"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return this.writeErrorResponse(exchange, "Missing or invalid Authorization header", "INVALID_ACCESS_TOKEN", HttpStatus.FORBIDDEN);
        }

        String token = authHeader.substring(7);

        try {
            boolean isValid = jwtService.validateToken(token, false);

            if (!isValid) {
                return this.writeErrorResponse(exchange, "Invalid access token", "INVALID_ACCESS_TOKEN", HttpStatus.FORBIDDEN);
            }

            String userId = jwtService.extractUserId(token, false);
            String role = jwtService.extractRole(token, false);

            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            return chain.filter(mutatedExchange);
        } catch (Exception e) {
            return this.writeErrorResponse(exchange, "Token validation failed", "INTERNAL_SERVICE_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, String message, String errorCode, HttpStatus status) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .message(message)
                .status(status.value())
                .errorCode(errorCode)
                .build();

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorResponse);
        } catch (Exception e) {
            bytes = ("{\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
