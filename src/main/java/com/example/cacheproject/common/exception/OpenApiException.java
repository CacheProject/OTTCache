package com.example.cacheproject.common.exception;

public class OpenApiException extends CustomException {

    public OpenApiException() {
        super(ErrorCode.OPEN_API_FETCH_FAILED);
    }

    public OpenApiException(String message) {
        super(ErrorCode.OPEN_API_FETCH_FAILED, message);
    }
}
