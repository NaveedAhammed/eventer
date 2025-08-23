package com.eventer.auth.exception;

import com.eventer.auth.dto.ErrorResponseDto;
import com.eventer.auth.exception.enums.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String s, Response response) {
        ErrorResponseDto errorResponseDto;

        try (InputStream body = response.body().asInputStream()) {
            log.debug("Body InputStream: {}", body);
            errorResponseDto = objectMapper.readValue(body, ErrorResponseDto.class);
        } catch (IOException e) {
            log.error("Error occurred while decoding error response", e);
            return new Exception(e.getMessage());
        }

        log.debug("Feign client exception: {}", errorResponseDto);

        if (Objects.equals(errorResponseDto.getErrorCode(), ErrorCode.USER_PROFILE_EXISTS.name())) {
            return new UserProfileExistsException(errorResponseDto.getMessage());
        }

        return new Exception("Unknown error occurred");
    }
}
