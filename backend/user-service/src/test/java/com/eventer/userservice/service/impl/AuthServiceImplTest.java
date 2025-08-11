package com.eventer.userservice.service.impl;

import com.eventer.userservice.dto.AuthResultDto;
import com.eventer.userservice.dto.LoginRequestDto;
import com.eventer.userservice.dto.RegisterRequestDto;
import com.eventer.userservice.entity.User;
import com.eventer.userservice.entity.enums.AuthProvider;
import com.eventer.userservice.entity.enums.Role;
import com.eventer.userservice.exception.InvalidCredentialsException;
import com.eventer.userservice.exception.ResourceNotFoundException;
import com.eventer.userservice.exception.UserAlreadyExistsException;
import com.eventer.userservice.repository.UserRepository;
import com.eventer.userservice.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    private RegisterRequestDto registerRequestDto;
    private LoginRequestDto loginRequestDto;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequestDto = RegisterRequestDto.builder()
                .email("test@gmail.com")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .role("USER")
                .build();
        loginRequestDto = LoginRequestDto.builder()
                .email("test@gmail.com")
                .password("password")
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

    @Test
    void testRegister_shouldReturnAuthResultDto_WhenUserIsNew() {
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

    @Test
    void testRegister_ShouldThrowException_WhenUserAlreadyExists() {
        when(userRepository.existsByEmail(registerRequestDto.getEmail())).thenReturn(true);

        Throwable thrown = catchThrowable(() -> authService.register(registerRequestDto));
        assertThat(thrown)
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with email test@gmail.com already exists");

        verify(userRepository).existsByEmail(registerRequestDto.getEmail());
    }

    @Test
    void testLogin_shouldReturnAuthResultDto_WhenUserExistsAndValidCredentials() {
        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), savedUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(savedUser, false)).thenReturn("accessToken");
        when(jwtService.generateToken(savedUser, true)).thenReturn("refreshToken");

        AuthResultDto authResultDto = authService.login(loginRequestDto);

        assertThat(authResultDto).isNotNull();
        assertThat(authResultDto.getAuthResponseDto()).isNotNull();
        assertThat(authResultDto.getAuthResponseDto().getAccessToken()).isEqualTo("accessToken");
        assertThat(authResultDto.getAuthResponseDto().getUser().getEmail()).isEqualTo(registerRequestDto.getEmail());
        assertThat(authResultDto.getAuthResponseDto().getUser().getFirstName()).isEqualTo(registerRequestDto.getFirstName());

        verify(userRepository).findByEmail(loginRequestDto.getEmail());
        verify(passwordEncoder).matches(loginRequestDto.getPassword(), savedUser.getPassword());
        verify(jwtService).generateToken(Mockito.any(User.class), eq(false));
        verify(jwtService).generateToken(Mockito.any(User.class), eq(true));
    }

    @Test
    void testLogin_ShouldThrowException_WhenUserDoesNotExists() {
        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> authService.login(loginRequestDto));

        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: test@gmail.com");

        verify(userRepository).findByEmail(loginRequestDto.getEmail());
    }

    @Test
    void testLogin_ShouldThrowException_WhenUserExistsAndInvalidCredentials() {
        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), savedUser.getPassword())).thenReturn(false);
        Throwable thrown = catchThrowable(() -> authService.login(loginRequestDto));

        assertThat(thrown)
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository).findByEmail(loginRequestDto.getEmail());
        verify(passwordEncoder).matches(loginRequestDto.getPassword(), savedUser.getPassword());
    }
}