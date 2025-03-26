package org.example.cacheproject.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message, HttpStatus.UNAUTHORIZED);
    }
}
