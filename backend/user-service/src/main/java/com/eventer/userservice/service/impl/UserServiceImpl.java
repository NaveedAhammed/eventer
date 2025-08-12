package com.eventer.userservice.service.impl;

import com.eventer.userservice.dto.UserDto;
import com.eventer.userservice.entity.User;
import com.eventer.userservice.mapper.UserMapper;
import com.eventer.userservice.repository.UserRepository;
import com.eventer.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto fetchMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        log.info("Authentication principal is {}", principal);
        String userId = ((UserDetails) principal).getUsername();

        if (userId == null) {
            throw new UsernameNotFoundException("Username not found");
        }

        User currentUser = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return UserMapper.toUserDto(currentUser);
    }
}
