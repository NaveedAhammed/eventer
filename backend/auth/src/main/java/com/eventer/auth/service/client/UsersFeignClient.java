package com.eventer.auth.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "users")
public interface UsersFeignClient {
    @PostMapping
    public ResponseEntity<Void> createUserProfile();
}
