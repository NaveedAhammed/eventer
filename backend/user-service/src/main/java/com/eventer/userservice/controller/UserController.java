package com.eventer.userservice.controller;

import com.eventer.userservice.dto.UserDto;
import com.eventer.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(){
        UserDto userDto = userService.fetchMe();
        return ResponseEntity.ok(userDto);
    }
}
