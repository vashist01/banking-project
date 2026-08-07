package com.banking.transaction.controller;

import com.banking.transaction.dto.request.TransactionRequest;
import com.banking.transaction.dto.response.TransactionResponse;
import com.banking.transaction.dto.response.TransactionSummaryResponse;
import com.banking.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> initiateTransaction(@RequestBody @Valid  TransactionRequest transactionRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.initiateTransaction(transactionRequest));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String transactionId){
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }
    @GetMapping("/transaction/{accountNumber}")
    private ResponseEntity<List<TransactionResponse>> getTransactionHistory(@PathVariable String accountNumber){
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber));
    }

    @PostMapping("/{transactionId}/verify")
    public ResponseEntity<TransactionResponse> verifyOTP(@PathVariable String transactionId,
                                                         @RequestParam String otp){
        return ResponseEntity.ok(transactionService.verifyOTP(transactionId,otp));
    }
    /**
     * GET /api/v1/transactions/summary - Get Transaction Summary
     */
    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryResponse> getTransactionSummary(
            @RequestParam String accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate) {

        log.info("Getting transaction summary for account: {}", accountId);

        TransactionSummaryResponse summary = transactionService
                .getTransactionSummary(accountId, startDate, endDate);

        return ResponseEntity.ok(summary);
    }
}
