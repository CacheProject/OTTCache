package com.example.cacheproject.common.response;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final Object data;

    private ErrorResponse(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, null);
    }

    public static ErrorResponse of(String code, String message, Object data) {
        return new ErrorResponse(code, message, data);
    }
}

