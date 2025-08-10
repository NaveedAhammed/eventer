package com.eventer.userservice.service;

import com.eventer.userservice.entity.User;
import com.eventer.userservice.entity.enums.AuthProvider;
import com.eventer.userservice.entity.enums.Role;
import com.eventer.userservice.exception.InternalServiceException;
import com.eventer.userservice.exception.UserAlreadyExistsException;
import com.eventer.userservice.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();

        String registrationId = token.getAuthorizedClientRegistrationId();
        AuthProvider authProvider = AuthProvider.fromValue(registrationId);

        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");

        if (email == null && authProvider == AuthProvider.GITHUB) {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    token.getAuthorizedClientRegistrationId(),
                    token.getName()
            );

            String accessToken = client.getAccessToken().getTokenValue();
            email = fetchGitHubPrimaryEmail(accessToken);
        }

        String avatar = authProvider == AuthProvider.GOOGLE ? oAuth2User.getAttribute("picture") : oAuth2User.getAttribute("avatar_url");

        if (email == null) {
            log.error("Email is not provided by: {}", authProvider);
            throw new InternalServiceException("Email is not provided by: " + authProvider);
        }

        Optional<User> existingAuthUser = userRepository.findByEmail(email);

        User user;

        if(existingAuthUser.isPresent()){
            user = existingAuthUser.get();
            if (user.getAuthProvider() != authProvider) {
                log.error("User with email {} already exists with authProvider {}", email, user.getAuthProvider());
                throw new UserAlreadyExistsException("User with email " + email + " already exists with authProvider " + user.getAuthProvider());
            }
        }else{
            String role = request.getSession().getAttribute(ROLE_PARAM).toString();

            user = User.builder()
                    .email(email)
                    .authProvider(authProvider)
                    .role(Role.fromValue(role))
                    .profilePictureUrl(avatar)
                    .verified(true)
                    .build();
            user = userRepository.save(user);
        }
        generateTokensAndRedirect(user, response);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("OAuth2 login failed", exception);

        String errorMessage = URLEncoder.encode("OAuth2 login failed: " + exception.getMessage(), StandardCharsets.UTF_8);
        String redirectUri = OAUTH2_FAILURE_REDIRECT_URI + "?error=" + errorMessage;

        response.sendRedirect(redirectUri);
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

    private String fetchGitHubPrimaryEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getBody() == null || response.getBody().isEmpty() || !response.getStatusCode().is2xxSuccessful()) {
            throw new InternalServiceException("Something went wrong. Please try again later.");
        }

        return response.getBody().stream()
                .filter(emailObj -> Boolean.TRUE.equals(emailObj.get("primary")))
                .map(emailObj -> (String) emailObj.get("email"))
                .findFirst()
                .orElse(null);
    }
}
