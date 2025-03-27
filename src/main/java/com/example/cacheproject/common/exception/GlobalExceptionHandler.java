package com.example.cacheproject.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.cacheproject.common.response.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("CustomException : {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage(), e.getStatus().value());
        return new ResponseEntity<>(errorResponse, e.getStatus());
    }

    // 데이터 정합성 예외 처리
    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityException(DataIntegrityException e) {
        log.error("DataIntegrityException : {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage(), e.getStatus().value());
        return new ResponseEntity<>(errorResponse, e.getStatus());
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException : {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("NULL_POINTER_EXCEPTION", "필수 값이 누락되었습니다.", 400);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException : {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("ILLEGAL_ARGUMENT", "잘못된 인자가 전달되었습니다.", 400);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.", 500);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
