package com.eventer.auth.service.client;

import com.eventer.auth.dto.CreateProfileDto;
import com.eventer.auth.exception.ServiceUnavailableException;
import com.eventer.auth.exception.UserProfileExistsException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "users", fallbackFactory = UsersFeignClient.UsersFeignFallback.class)
public interface UsersFeignClient {
    @PostMapping("/api/users/create-profile")
    ResponseEntity<Void> createProfile(@RequestBody CreateProfileDto createProfileDto);

    @Component
    class UsersFeignFallback implements FallbackFactory<UsersFeignClient> {
        @Override
        public UsersFeignClient create(Throwable cause) {
            return new UsersFeignClient() {
                @Override
                public ResponseEntity<Void> createProfile(CreateProfileDto createProfileDto) {
                    if (cause instanceof UserProfileExistsException) {
                        throw (UserProfileExistsException) cause;
                    } else {
                        throw new ServiceUnavailableException("Users service is unavailable. Please try again later.");
                    }
                }
            };
        }
    }
}
