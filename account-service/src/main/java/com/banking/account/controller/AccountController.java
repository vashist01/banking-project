package com.banking.account.controller;

import com.banking.account.dto.request.CreateAccountRequest;
import com.banking.account.dto.request.FreezeRequest;
import com.banking.account.dto.response.AccountResponse;
import com.banking.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create-account")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody  @Valid  CreateAccountRequest createAccountRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(createAccountRequest));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber){
        return ResponseEntity.ok(accountService.getAccount(accountNumber));
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNumber){
        return ResponseEntity.ok(accountService.getBalance(accountNumber));
    }

    @PutMapping("/{accountNumber}/block")
    public ResponseEntity<String> blockAccount(@PathVariable String accountNumber){
        accountService.blockAccount(accountNumber);
        return ResponseEntity.ok("Account successfully blocked");
    }
    @PutMapping("/{accountNumber}/deduct-balance")
    public ResponseEntity<String> deductBalance(@PathVariable String accountNumber,
                                                @RequestParam BigDecimal amount){
        accountService.deductBalance(accountNumber,amount);
        return ResponseEntity.ok("Balance Deduct Successfully");

    }

    @PutMapping("/{accountNumber}/credit-balance")
    public ResponseEntity<String> creditBalance(@PathVariable String accountNumber,
                                                @RequestParam BigDecimal amount){
        accountService.creditBalance(accountNumber,amount);
        return ResponseEntity.ok("Balance Deduct Successfully");

    }

    /**
     * POST /api/v1/accounts/{id}/freeze - Freeze account
     * Freezes an account, preventing all transactions
     */
    @PostMapping("/freeze")
    public ResponseEntity<AccountResponse> freezeAccount(
            @Valid @RequestBody FreezeRequest request) {
        log.info("Received request to freeze account: {}, reason: {}",request.getReason());
        AccountResponse response = accountService.freezeAccount(request);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/accounts/{id}/unfreeze - Unfreeze account
     * Unfreezes a previously frozen account
     */
    @PostMapping("/{id}/unfreeze")
    public ResponseEntity<AccountResponse> unfreezeAccount(@PathVariable String accountNumber) {
        log.info("Received request to unfreeze account: {}", accountNumber);

        AccountResponse response = accountService.unfreezeAccount(accountNumber);

        return ResponseEntity.ok(response);
    }


}
