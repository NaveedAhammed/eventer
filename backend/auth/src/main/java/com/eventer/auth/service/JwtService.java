package com.eventer.auth.service;

import com.eventer.auth.entity.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import static com.eventer.auth.constants.AuthConstants.ROLE_PARAM;

@Slf4j
@Service
public class JwtService {
    @Value("${jwt.access.token.expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${jwt.refresh.token.expiration}")
    private long REFRESH_EXPIRATION;

    private final Key refreshKey;
    private final Key accessKey;

    public JwtService(
            @Value("${jwt.access.token.secret}") String ACCESS_SECRET,
            @Value("${jwt.refresh.token.secret}") String REFRESH_SECRET
    ){
        refreshKey = Keys.hmacShaKeyFor(REFRESH_SECRET.getBytes(StandardCharsets.UTF_8));
        accessKey = Keys.hmacShaKeyFor(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(AuthUser authUser, boolean isRefreshToken) {
        long expiration = isRefreshToken ? REFRESH_EXPIRATION : ACCESS_EXPIRATION;
        Key key = isRefreshToken ? refreshKey : accessKey;

        return Jwts.builder()
                .claim(ROLE_PARAM,  authUser.getRole())
                .subject(authUser.getAuthUserId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }
}
