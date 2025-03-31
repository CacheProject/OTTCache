package com.example.cacheproject.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    AUTHORIZATION(HttpStatus.UNAUTHORIZED,"AUTHORIZATION","인증이 필요합니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"BAD_REQUEST","잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND,"NOT_FOUND","찾지 못했습니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN,"FORBIDDEN","권한이 없습니다"),
    OPEN_API_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OPEN_API_FETCH_FAILED", "OpenAPI 데이터를 가져오는 데 실패했습니다."),
    CSV_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CSV_FETCH_FAILED", "CSV 데이터를 처리하는 데 실패했습니다."),
    DATA_INCONSISTENCY(HttpStatus.INTERNAL_SERVER_ERROR, "DATA_INCONSISTENCY", "데이터 정합성 오류입니다.");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String code, String defaultMessage) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
