package com.example.cacheproject.domain.user.dto.response;

import com.example.cacheproject.domain.store.dto.StoreSummaryDto;
import com.example.cacheproject.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.example.cacheproject.domain.user.entity.User;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserProfileResponseDto {

    private final Long id;
    private final String username;
    private final String role;
    private final LocalDateTime createdAt;
    private final StoreSummaryDto store;

    public static UserProfileResponseDto from(User user, Store store) {
        StoreSummaryDto storeDto = (store != null) ? new StoreSummaryDto(store.getStore_name(), store.getEmail()) : null;
        return new UserProfileResponseDto(user.getId(), user.getUsername(), user.getUserRole().name(), user.getCreatedAt(), storeDto);
    }
}
