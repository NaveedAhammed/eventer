package com.eventer.auth.service;

import com.eventer.auth.dto.AuthTokensDto;
import com.eventer.auth.dto.OAuth2UserInfo;
import com.eventer.auth.util.OAuth2UserInfoFactory;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.eventer.auth.constants.AuthConstants.ACCESS_TOKEN_PARAM;
import static com.eventer.auth.constants.AuthConstants.REFRESH_TOKEN_PARAM;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2HandlerService implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    @Value("${spring.security.oauth2.success.redirect.uri}")
    private String OAUTH2_SUCCESS_REDIRECT_URI;

    @Value("${spring.security.oauth2.failure.redirect.uri}")
    private String OAUTH2_FAILURE_REDIRECT_URI;

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2UserInfo userInfo = oAuth2UserInfoFactory.getOAuth2UserInfo(
                request,
                (OAuth2AuthenticationToken) authentication,
                (OAuth2User) authentication.getPrincipal(),
                authorizedClientService
        );

        try {
            AuthTokensDto authTokensDto = authService.oAuth2(userInfo);

            Cookie cookie = new Cookie(REFRESH_TOKEN_PARAM, authTokensDto.getRefreshToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");

            response.addCookie(cookie);

            String redirectUri = UriComponentsBuilder.fromUriString(OAUTH2_SUCCESS_REDIRECT_URI)
                    .queryParam(ACCESS_TOKEN_PARAM, authTokensDto.getAccessToken())
                    .build().toString();

            response.sendRedirect(redirectUri);
        }catch (Exception ex){
            onException(response, ex);
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        onException(response, exception);
    }

    private void onException(HttpServletResponse response, Exception exception) throws IOException {
        log.debug("OAuth2 login failed", exception);

        String errorMessage = URLEncoder.encode("OAuth2 login failed: " + exception.getMessage(), StandardCharsets.UTF_8);
        String redirectUri = OAUTH2_FAILURE_REDIRECT_URI + "?error=" + errorMessage;

        response.sendRedirect(redirectUri);
    }
}
