package com.eventer.users.service;

import com.eventer.users.dto.CreateProfileDto;

public interface UserService {
    /**
     * Create a user profile based on the provided CreateProfileDto.
     *
     * @param createProfileDto the {@link CreateProfileDto} object containing user profile information.
     */
    void createUserProfile(CreateProfileDto createProfileDto);
}
