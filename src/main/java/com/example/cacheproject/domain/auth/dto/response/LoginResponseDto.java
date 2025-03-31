package com.example.cacheproject.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponseDto {

    private final String accessToken;
    private final Long id;
    private final String email;
    private final String name;
    private final String role;
}
