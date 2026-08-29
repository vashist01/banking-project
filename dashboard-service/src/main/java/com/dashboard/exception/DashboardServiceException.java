package com.dashboard.exception;

public class DashboardServiceException extends RuntimeException {

    public DashboardServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}