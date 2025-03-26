package org.example.cacheproject.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super("NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
