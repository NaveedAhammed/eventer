package com.eventer.userservice.service.impl;

import com.eventer.userservice.dto.AuthResultDto;
import com.eventer.userservice.dto.RegisterRequestDto;
import com.eventer.userservice.entity.User;
import com.eventer.userservice.entity.enums.AuthProvider;
import com.eventer.userservice.entity.enums.Role;
import com.eventer.userservice.exception.UserAlreadyExistsException;
import com.eventer.userservice.repository.UserRepository;
import com.eventer.userservice.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthServiceImpl authService;
    private AutoCloseable autoCloseable;
    private RegisterRequestDto registerRequestDto;
    private User savedUser;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        registerRequestDto = RegisterRequestDto.builder()
                .email("test@gmail.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .role("USER")
                .build();
        savedUser = User.builder()
                .userId(UUID.randomUUID())
                .firstName("firstName")
                .email("test@gmail.com")
                .password("hashedPassword")
                .role(Role.USER)
                .authProvider(AuthProvider.LOCAL)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testRegisterNewUser_Success() {
        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode(registerRequestDto.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser, false)).thenReturn("accessToken");
        when(jwtService.generateToken(savedUser, true)).thenReturn("refreshToken");

        AuthResultDto authResultDto = authService.register(registerRequestDto);

        assertThat(authResultDto).isNotNull();
        assertThat(authResultDto.getAuthResponseDto()).isNotNull();
        assertThat(authResultDto.getAuthResponseDto().getAccessToken()).isEqualTo("accessToken");
        assertThat(authResultDto.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(authResultDto.getAuthResponseDto().getUser().getEmail()).isEqualTo(registerRequestDto.getEmail());
        assertThat(authResultDto.getAuthResponseDto().getUser().getFirstName()).isEqualTo(registerRequestDto.getFirstName());

        verify(userRepository).existsByEmail(registerRequestDto.getEmail());
        verify(passwordEncoder).encode(registerRequestDto.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(Mockito.any(User.class), eq(false));
        verify(jwtService).generateToken(Mockito.any(User.class), eq(true));
    }
}