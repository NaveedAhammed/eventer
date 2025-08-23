package com.eventer.auth.util;

import com.eventer.auth.dto.OAuth2UserInfo;
import com.eventer.auth.entity.enums.AuthProvider;
import com.eventer.auth.entity.enums.Role;
import com.eventer.auth.exception.InternalServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.eventer.auth.constants.AuthConstants.ROLE_PARAM;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2UserInfoFactory {

    @Value("${spring.security.oauth2.github.users.email.url}")
    private String GITHUB_USERS_EMAIL_URL;

    private final RestTemplate restTemplate;

    public OAuth2UserInfo getOAuth2UserInfo(HttpServletRequest request, OAuth2AuthenticationToken token, OAuth2User user, OAuth2AuthorizedClientService clientService){
        String registrationId = token.getAuthorizedClientRegistrationId();
        AuthProvider authProvider = AuthProvider.fromValue(registrationId);

        String username = user.getAttribute("name");
        String email = user.getAttribute("email");
        String profilePictureUrl = authProvider == AuthProvider.GOOGLE ? user.getAttribute("picture") : user.getAttribute("avatar_url");
        Role role = Role.fromValue(request.getSession().getAttribute(ROLE_PARAM).toString());

        if (authProvider == AuthProvider.GITHUB) {
            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(registrationId, token.getName());
            email = fetchGitHubPrimaryEmail(client.getAccessToken().getTokenValue());
        }

        if (email == null) {
            throw new InternalServiceException("Email is not provided by: " + authProvider.name());
        }

        return OAuth2UserInfo.builder()
                .email(email)
                .username(username)
                .profilePictureUrl(profilePictureUrl)
                .authProvider(authProvider)
                .role(role)
                .build();
    }

    private String fetchGitHubPrimaryEmail(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                GITHUB_USERS_EMAIL_URL,
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
