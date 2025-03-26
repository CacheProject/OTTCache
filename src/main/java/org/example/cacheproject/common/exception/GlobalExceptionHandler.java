package org.example.cacheproject.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.cacheproject.common.response.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("입력값이 유효하지 않습니다.");
        return ErrorResponse.of("VALIDATION_ERROR", errorMessage);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return ErrorResponse.of("UNIQUE_ERROR", "존재하는 데이터입니다.");
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.info("CustomException : {}", e.getMessage(), e);
        return new ResponseEntity<>(ErrorResponse.of(e.getErrorCode(), e.getMessage()), e.getStatus());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGlobalException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return ErrorResponse.of("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.");
    }
}
