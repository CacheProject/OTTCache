package com.example.plusproject.exception;

public class DataIntegrityException extends CustomException {

    public DataIntegrityException() { super(ErrorCode.DATA_INCONSISTENCY); }

    public DataIntegrityException(String message) {
        super(ErrorCode.DATA_INCONSISTENCY, message);
    }
}