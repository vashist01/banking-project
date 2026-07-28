package com.banking.transaction.event;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record TransactionDetails(long transactionId,
                               String accountNumber,
                               BigDecimal amount,
                               String otp,
                               String reason) {
}
