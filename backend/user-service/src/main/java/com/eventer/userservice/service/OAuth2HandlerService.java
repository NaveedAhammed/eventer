package com.eventer.userservice.service;

import com.eventer.userservice.dto.OAuth2UserInfo;
import com.eventer.userservice.entity.User;
import com.eventer.userservice.exception.InternalServiceException;
import com.eventer.userservice.mapper.UserMapper;
import com.eventer.userservice.repository.UserRepository;
import com.eventer.userservice.utils.OAuth2UserInfoFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.eventer.userservice.constants.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2HandlerService implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    @Value("${spring.security.oauth2.success.redirect.uri}")
    private String OAUTH2_SUCCESS_REDIRECT_URI;

    @Value("${spring.security.oauth2.failure.redirect.uri}")
    private String OAUTH2_FAILURE_REDIRECT_URI;

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2UserInfo userInfo = oAuth2UserInfoFactory.getOAuth2UserInfo(
                request,
                (OAuth2AuthenticationToken) authentication,
                (OAuth2User) authentication.getPrincipal(),
                authorizedClientService
        );

        User user = findOrCreateUser(userInfo);
        generateTokensAndRedirect(user, response);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("OAuth2 login failed", exception);

        String errorMessage = URLEncoder.encode("OAuth2 login failed: " + exception.getMessage(), StandardCharsets.UTF_8);
        String redirectUri = OAUTH2_FAILURE_REDIRECT_URI + "?error=" + errorMessage;

        response.sendRedirect(redirectUri);
    }

    private User findOrCreateUser(OAuth2UserInfo oAuth2UserInfo) {
        User user = userRepository.findByEmail(oAuth2UserInfo.getEmail()).orElse(null);

        if (user != null) {
            if (user.getAuthProvider() != oAuth2UserInfo.getAuthProvider()){
                throw new InternalServiceException("User already exists with auth provider " + oAuth2UserInfo.getAuthProvider().name());
            }
            return user;
        }
        user = UserMapper.toUser(oAuth2UserInfo);
        user = userRepository.save(user);
        return user;
    }

    private void generateTokensAndRedirect(User user, HttpServletResponse response) throws IOException {
        String accessToken = jwtService.generateToken(user, false);
        String refreshToken = jwtService.generateToken(user, true);

        Cookie cookie = new Cookie(REFRESH_TOKEN_PARAM, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");

        response.addCookie(cookie);

        String redirectUri = UriComponentsBuilder.fromUriString(OAUTH2_SUCCESS_REDIRECT_URI)
                .queryParam(ACCESS_TOKEN_PARAM, accessToken)
                .build().toString();

        response.sendRedirect(redirectUri);
    }
}
