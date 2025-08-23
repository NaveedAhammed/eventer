package com.eventer.users.service.impl;

import com.eventer.users.dto.CreateProfileDto;
import com.eventer.users.dto.UserDto;
import com.eventer.users.entity.User;
import com.eventer.users.exception.ResourceNotFoundException;
import com.eventer.users.exception.UserProfileExistsException;
import com.eventer.users.mapper.UserMapper;
import com.eventer.users.repository.UserRepository;
import com.eventer.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Create a user profile based on the provided CreateProfileDto.
     *
     * @param createProfileDto the {@link CreateProfileDto} object containing user profile information.
     */
    @Override
    public void createUserProfile(CreateProfileDto createProfileDto) {
        if (userRepository.existsByEmailOrUsername(createProfileDto.getEmail(), createProfileDto.getUsername())) {
            log.debug("User profile already exists with email {} or username {}", createProfileDto.getEmail(), createProfileDto.getUsername());
            throw new UserProfileExistsException("User profile already exists with email " + createProfileDto.getEmail() + " or username " + createProfileDto.getUsername());
        }

        User user = UserMapper.toUser(createProfileDto);
        userRepository.save(user);
        log.debug("User profile created successfully for email {}", createProfileDto.getEmail());
    }

    /**
     * Retrieve a user profile by user ID.
     *
     * @param userId the ID of the user whose profile is to be fetched.
     * @return the {@link UserDto} containing user profile information.
     */
    @Override
    public UserDto getUserProfileById(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        return UserMapper.toUserDto(user);
    }


}
