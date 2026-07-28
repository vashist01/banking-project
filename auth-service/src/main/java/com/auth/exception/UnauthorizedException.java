package com.auth.exception;

public class UnauthorizedException extends RuntimeException{
    private final String path;

    public UnauthorizedException(String message) {
        super(message);
        this.path = null;
    }

    public UnauthorizedException(String message, String path) {
        super(message);
        this.path = path;
    }
}
