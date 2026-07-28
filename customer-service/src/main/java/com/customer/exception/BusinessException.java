package com.customer.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private String path;
    private String code;

    public BusinessException(String path,String code){
        this.path = path;
        this.code = code;
    }

   public BusinessException(String message,String path,String code){
        super(message);
        this.path = path;
        this.code = code;
    }

    public BusinessException(String message){
        super(message);
    }
}
