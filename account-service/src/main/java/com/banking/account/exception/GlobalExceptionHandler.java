package com.banking.account.exception;

import com.banking.account.dto.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccountNotFound(
            AccountNotFoundException e) {
        log.error("Account not found: {}", e.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(e.getMessage(), 404));
    }
    
    @ExceptionHandler(AccountValidationException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccountValidation(
            AccountValidationException e) {
        log.error("Account validation failed: {}", e.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), 400));
    }
    
    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<BaseResponse<Void>> handleDuplicateAccount(
            DuplicateAccountException e) {
        log.error("Duplicate account: {}", e.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.error(e.getMessage(), 409));
    }
    
//    @ExceptionHandler(InsufficientBalanceException.class)
//    public ResponseEntity<BaseResponse<Void>> handleInsufficientBalance(
//            InsufficientBalanceException e) {
//        log.error("Insufficient balance: {}", e.getMessage());
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(BaseResponse.error(e.getMessage(), 400));
//    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalState(
            IllegalStateException e) {
        log.error("Illegal state: {}", e.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getMessage(), 400));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException e) {
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("Validation errors: {}", errors);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error("Validation failed: " + errors, 400));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGenericException(
            Exception e) {
        log.error("Unexpected error occurred", e);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(
                    "An unexpected error occurred. Please try again later.", 
                    500
                ));
    }
}