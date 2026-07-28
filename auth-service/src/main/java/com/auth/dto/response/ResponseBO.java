package com.auth.dto.response;

public record ResponseBO(Object data,String message,int statusCode,String dateTime,String sessionId) {
}
