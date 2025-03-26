package org.example.cacheproject.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private String message;
    private T data;

    public static <T> ApiResponse<T> of(String message, T data) {
        return new ApiResponse<>(message, data);
    }
}
