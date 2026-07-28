package com.banking.account.exception;

public class AccountValidationException extends RuntimeException{
    private String message;
    private String statusCode;
    public AccountValidationException(String message,String statusCode){
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
