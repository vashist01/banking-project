package com.banking.account.exception;

public class TransferValidationException extends RuntimeException{
    private String message;

    public TransferValidationException(String message){
        super(message);
    }
}
