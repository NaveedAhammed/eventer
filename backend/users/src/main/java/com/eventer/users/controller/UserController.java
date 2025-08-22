package com.eventer.users.controller;

import com.eventer.users.dto.CreateProfileDto;
import com.eventer.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/create-profile")
    public ResponseEntity<Void> createProfile(@RequestBody CreateProfileDto createProfileDto) {
        log.debug("CreateProfileDto : {}", createProfileDto);
        userService.createUserProfile(createProfileDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
