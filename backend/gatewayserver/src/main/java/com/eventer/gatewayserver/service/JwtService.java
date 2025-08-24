package com.eventer.gatewayserver.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.function.Function;

import static com.eventer.gatewayserver.constants.GatewayServerConstants.ROLE_PARAM;

@Slf4j
@Service
public class JwtService {

    private final Key refreshKey;
    private final Key accessKey;

    public JwtService(
            @Value("${jwt.access.token.secret}") String ACCESS_SECRET,
            @Value("${jwt.refresh.token.secret}") String REFRESH_SECRET
    ){
        refreshKey = Keys.hmacShaKeyFor(REFRESH_SECRET.getBytes(StandardCharsets.UTF_8));
        accessKey = Keys.hmacShaKeyFor(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token, boolean isRefreshToken) {
        Key key = isRefreshToken ? refreshKey : accessKey;
        try {
            log.info("Validating token: {}", token);

            if (token == null) return false;

            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (Exception e) {
            log.error("Invalid token {}", e.getMessage());
            return false;
        }
    }

    public String extractUserId(String token, boolean isRefreshToken) {
        return extractClaim(token, Claims::getSubject, isRefreshToken);
    }

    public String extractRole(String token, boolean isRefreshToken) {
        return extractClaim(token, claims -> claims.get(ROLE_PARAM, String.class), isRefreshToken);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, boolean isRefreshToken) {
        Key key = isRefreshToken ? refreshKey : accessKey;
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

}
