package org.example.cacheproject.common.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public CustomException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
