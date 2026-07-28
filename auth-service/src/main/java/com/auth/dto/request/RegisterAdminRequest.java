package com.auth.dto.request;

import lombok.Data;

@Data
public class RegisterAdminRequest {
    private String username;
    private String password;
    private String email;
}