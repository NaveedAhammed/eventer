package com.eventer.users.service.impl;

import com.eventer.users.dto.CreateProfileDto;
import com.eventer.users.entity.User;
import com.eventer.users.exception.UserProfileExistsException;
import com.eventer.users.mapper.UserMapper;
import com.eventer.users.repository.UserRepository;
import com.eventer.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        if (userRepository.existsByEmail(createProfileDto.getEmail())) {
            log.debug("User profile with email {} already exists", createProfileDto.getEmail());
            throw new UserProfileExistsException("User profile with email " + createProfileDto.getEmail() + " already exists");
        }

        User user = UserMapper.toUser(createProfileDto);
        userRepository.save(user);
        log.debug("User profile created successfully for email {}", createProfileDto.getEmail());
    }


}
