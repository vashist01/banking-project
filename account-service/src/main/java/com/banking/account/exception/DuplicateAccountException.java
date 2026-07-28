package com.banking.account.exception;

public class DuplicateAccountException extends RuntimeException {
    private String message;
    private String statusCode;
    public DuplicateAccountException(String message,String statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
