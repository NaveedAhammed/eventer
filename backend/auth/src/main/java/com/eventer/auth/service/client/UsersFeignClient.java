package com.eventer.auth.service.client;

import com.eventer.auth.dto.CreateProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "users", url = "http://localhost:8090")
public interface UsersFeignClient {
    @PostMapping("/api/users/create-profile")
    ResponseEntity<Void> createProfile(@RequestBody CreateProfileDto createProfileDto);
}
