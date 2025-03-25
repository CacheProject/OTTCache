package com.example.plusproject.exception;

public class BusinessException extends CustomException {

  public BusinessException() { super(ErrorCode.BUSINESS_ERROR);}

  public BusinessException(String message) { super(ErrorCode.BAD_REQUEST, message); }
}