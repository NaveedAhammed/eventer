package com.eventer.users.service;

import com.eventer.users.dto.CreateProfileDto;
import com.eventer.users.dto.UserDto;

public interface UserService {
    /**
     * Create a user profile based on the provided CreateProfileDto.
     *
     * @param createProfileDto the {@link CreateProfileDto} object containing user profile information.
     */
    void createUserProfile(CreateProfileDto createProfileDto);

    /**
     * Retrieve a user profile by user ID.
     *
     * @param userId the ID of the user whose profile is to be fetched.
     * @return the {@link UserDto} containing user profile information.
     */
    UserDto getUserProfileById(String userId);
}
