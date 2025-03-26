package com.example.cacheproject.domain.user.dto.response;

import lombok.AllArgsConstructor;
import com.example.cacheproject.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserListResponseDto {

    private final Long id;
    private final String username;
    private final String role;
    private final LocalDateTime createdAt;

    public static UserListResponseDto fromEntity(User user) {
        return new UserListResponseDto(user.getId(), user.getUsername(), user.getUserRole().name(), user.getCreatedAt());
    }
}
