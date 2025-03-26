package org.example.cacheproject.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CustomException {

    public BadRequestException(String message) {
        super("BAD_REQUEST", message, HttpStatus.BAD_REQUEST);
    }
}
