package com.example.cacheproject.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupResponseDto {

    private final String message;
    private final Long userId;
    private final Long storeId;
}
