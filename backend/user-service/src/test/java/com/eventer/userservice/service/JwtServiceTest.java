package com.eventer.userservice.service;

import com.eventer.userservice.entity.User;
import com.eventer.userservice.entity.enums.AuthProvider;
import com.eventer.userservice.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private JwtService jwtService;

    private final UUID userId = UUID.randomUUID();
    private User user;

    @BeforeEach
    void setUp() {
        String ACCESS_SECRET = "access-secret-key-access-secret-key";
        String REFRESH_SECRET = "refresh-secret-key-refresh-secret-key";
        jwtService = new JwtService(ACCESS_SECRET, REFRESH_SECRET);

        // Set expiration manually since @Value won't inject in tests
        ReflectionTestUtils.setField(jwtService, "ACCESS_EXPIRATION", 1000L * 60 * 15); // 15 mins
        ReflectionTestUtils.setField(jwtService, "REFRESH_EXPIRATION", 1000L * 60 * 60 * 24 * 7); // 7 days

        user = User.builder()
                .userId(userId)
                .email("test@gmail.com")
                .password("password")
                .firstName("firstName")
                .authProvider(AuthProvider.LOCAL)
                .role(Role.USER)
                .build();
    }

    @Test
    void testGenerateToken_ForAccessToken() {
        String token = jwtService.generateToken(user, false);

        assertThat(token).isNotNull();
        assertThat(jwtService.validateToken(token, false)).isTrue();
        assertThat(jwtService.extractUserId(token, false)).isEqualTo(userId.toString());
        assertThat(jwtService.extractRole(token, false)).isEqualTo(Role.USER.name());
    }

    @Test
    void testGenerateToken_ForRefreshToken() {
        String token = jwtService.generateToken(user, true);

        assertThat(token).isNotNull();
        assertThat(jwtService.validateToken(token, true)).isTrue();
        assertThat(jwtService.extractUserId(token, true)).isEqualTo(userId.toString());
        assertThat(jwtService.extractRole(token, true)).isEqualTo(Role.USER.name());
    }

    @Test
    void testGetJwtFromHeader() {
        String token = "sample.jwt.token";
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("Authorization", "Bearer " + token);

        String expectedJwt = jwtService.getJwtFromHeader(mockHttpServletRequest);

        assertThat(expectedJwt).isNotNull();
        assertThat(expectedJwt).isEqualTo(token);
    }

    @Test
    void testGetJwtFromHeader_ShouldReturnNullIfNoTokenPresent() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        String expectedJwt = jwtService.getJwtFromHeader(mockHttpServletRequest);
        assertThat(expectedJwt).isNull();
    }

    @Test
    void testGetJwtFromHeader_ShouldReturnNullForNonBearerTokenFormat() {
        String token = "sample.jwt.token";
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("Authorization", "Basic " + token);

        String expectedJwt = jwtService.getJwtFromHeader(mockHttpServletRequest);

        assertThat(expectedJwt).isNull();
    }

    @Test
    void testValidateToken_ShouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.jwt.token";
        boolean isValid = jwtService.validateToken(invalidToken, false);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateToken_ShouldReturnFalseIfTokenIsNull() {
        boolean isValid = jwtService.validateToken(null, false);
        assertThat(isValid).isFalse();
    }
}
