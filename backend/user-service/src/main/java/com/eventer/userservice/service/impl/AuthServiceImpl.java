package com.eventer.userservice.service.impl;

import com.eventer.userservice.dto.*;
import com.eventer.userservice.entity.User;
import com.eventer.userservice.exception.InvalidCredentialsException;
import com.eventer.userservice.exception.ResourceNotFoundException;
import com.eventer.userservice.exception.UserAlreadyExistsException;
import com.eventer.userservice.mapper.UserMapper;
import com.eventer.userservice.repository.UserRepository;
import com.eventer.userservice.service.AuthService;
import com.eventer.userservice.service.JwtService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     *
     * @param requestDto RegisterRequestDto Object
     * @return AuthResultDto Object which consist of accessToken, refreshToken and UserDto
     */
    @Override
    public AuthResultDto register(RegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            log.info("User with email {} already exists", requestDto.getEmail());
            throw new UserAlreadyExistsException("User with email " + requestDto.getEmail() + " already exists");
        }

        User user = UserMapper.toUser(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user = userRepository.save(user);

        UserDto userDto = UserMapper.toUserDto(user);

        String accessToken = jwtService.generateToken(user, false);
        String refreshToken = jwtService.generateToken(user, true);

        return AuthResultDto.builder()
                .authResponseDto(AuthResponseDto.builder()
                        .accessToken(accessToken)
                        .user(userDto)
                        .build()
                )
                .refreshToken(refreshToken)
                .build();
    }

    /**
     *
     * @param requestDto LoginRequestDto Object
     * @return AuthResultDto Object which consist of accessToken, refreshToken and UserDto
     */
    @Override
    public AuthResultDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", requestDto.getEmail()));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        UserDto userDto = UserMapper.toUserDto(user);

        String accessToken = jwtService.generateToken(user, false);
        String refreshToken = jwtService.generateToken(user, true);

        return AuthResultDto.builder()
                .authResponseDto(AuthResponseDto.builder()
                        .accessToken(accessToken)
                        .user(userDto)
                        .build()
                )
                .refreshToken(refreshToken)
                .build();
    }
}
