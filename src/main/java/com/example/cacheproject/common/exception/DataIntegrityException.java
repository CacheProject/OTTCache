package com.example.cacheproject.common.exception;

public class DataIntegrityException extends CustomException {

    public DataIntegrityException() { super(ErrorCode.DATA_INCONSISTENCY); }

    public DataIntegrityException(String message) {
        super(ErrorCode.DATA_INCONSISTENCY, message);
    }
}