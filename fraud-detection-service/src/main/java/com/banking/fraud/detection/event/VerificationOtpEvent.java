package com.banking.fraud.detection.event;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record VerificationOtpEvent(
        String transactionId,
        String accountNumber,
        BigDecimal amount,
        String reason,
        boolean isFraud
) {
}
