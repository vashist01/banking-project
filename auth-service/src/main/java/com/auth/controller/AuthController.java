package com.auth.controller;

import com.auth.dto.request.*;
import com.auth.dto.response.AuthResponse;
import com.auth.enums.ApiVersionEnum;
import com.auth.service.AuthService;
import com.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiVersionEnum.PREFIX_API_URL)
@RequiredArgsConstructor
@Slf4j
@Validated
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        AuthResponse response = authService.registerAdmin(
                request.getUsername(),
                request.getPassword(),
                request.getEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", isValid);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-context")
    public ResponseEntity<UserContext> getUserContext(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer "
        UserContext context = authService.getUserContextFromToken(token);
        return ResponseEntity.ok(context);
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        authService.logout(token);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequest request) {
        String token = authHeader.substring(7);
        authService.changePassword(token, request.getOldPassword(), request.getNewPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign-customer")
    public ResponseEntity<String> assignCustomerId(
            @RequestParam String email,
            @RequestParam String customerId) {
        authService.assignCustomerId(email, customerId);
        return ResponseEntity.ok("Customer ID assigned successfully");
    }

    @PostMapping("/unlock-account")
    public ResponseEntity<Map<String, String>> unlockAccount(@RequestParam String email) {
        userService.unlockAccount(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Account unlocked successfully");
        return ResponseEntity.ok(response);
    }
}

