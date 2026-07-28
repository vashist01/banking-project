package com.auth.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final String path;
    private final String errorCode;

    public BusinessException(String message,String path, String errorCode) {
        super(message);
        this.path = path;
        this.errorCode = errorCode;
    }
    public BusinessException(String message) {
        super(message);
        this.path = null;
        this.errorCode = "BUSINESS_ERROR";
    }

    public BusinessException(String message, String errorCode) {
        super(message);
        this.path = null;
        this.errorCode = errorCode;
    }
}
