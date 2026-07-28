package com.banking.transaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "account-service",url = "${account.service.url}")
public interface TransactionServiceClient {
    @PutMapping("/api/v1/account/{accountNumber}/deduct-balance")
    String deductBalance(@PathVariable String accountNumber,
                         @RequestParam BigDecimal amount);

    @PutMapping("/ap1/v1/account/{accountNumber}/credit-balance")
    String creditBalance(@PathVariable String accountNumber,
                         @RequestParam BigDecimal amount);
}
