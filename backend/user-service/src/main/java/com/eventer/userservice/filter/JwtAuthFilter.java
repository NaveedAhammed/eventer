package com.eventer.userservice.filter;

import com.eventer.userservice.exception.InvalidAccessTokenException;
import com.eventer.userservice.exception.UserUnAuthenticatedException;
import com.eventer.userservice.exception.enums.ErrorCode;
import com.eventer.userservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.eventer.userservice.constants.Constants.REFRESH_TOKEN_PARAM;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_URLS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/login"
    );

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("JwtAuthFilter called for URI: {}", requestURI);

        if (PUBLIC_URLS.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = jwtService.getJwtFromHeader(request);
            String refreshToken = extractRefreshToken(request);

            validateAccessToken(accessToken);
            validateRefreshToken(refreshToken);

            setSecurityContext(accessToken, request);

            filterChain.doFilter(request, response);
        } catch (InvalidAccessTokenException ex){
            log.error(ex.getMessage());

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(String.format("{\"message\": \"%s\", \"errorCode\": \"%s\"}", ex.getMessage(), ErrorCode.INVALID_ACCESS_TOKEN));
        } catch (UserUnAuthenticatedException ex){
            log.error(ex.getMessage());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(String.format("{\"message\": \"%s\", \"errorCode\": \"%s\"}", ex.getMessage(), ErrorCode.UN_AUTHENTICATED));
        } catch (Exception ex) {
            log.error("Exception in jwt auth filter: {}", ex.getMessage());

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write(
                    String.format("{\"message\": \"%s\", \"errorCode\": \"%s\"}", ex.getMessage(), ErrorCode.INTERNAL_SERVICE_ERROR)
            );
        }
    }

    private String extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> REFRESH_TOKEN_PARAM.equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElse(null);
    }

    private void validateRefreshToken(String refreshToken) {
        log.info("Refresh Token: {}", refreshToken);
        if (refreshToken == null || !jwtService.validateToken(refreshToken, true)) {
            throw new UserUnAuthenticatedException("Invalid refresh token");
        }
    }

    private void validateAccessToken(String accessToken) {
        log.info("Access Token: {}", accessToken);
        if (accessToken == null || !jwtService.validateToken(accessToken, false)) {
            throw new InvalidAccessTokenException("Invalid access token");
        }
    }

    private void setSecurityContext(String accessToken, HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) return;

        String email = jwtService.extractUserId(accessToken, false);
        String role = jwtService.extractRole(accessToken, false);

        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        UserDetails userDetails = User.withUsername(email).password("").roles(authority.getAuthority()).build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Authenticated user: {} with role: {}", email, role);
    }
}
