package com.banking.transaction.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionCompletedEvent(String transactionId,
                                        String senderAccountNumber,
                                        String receiverAccountNumber,
                                        BigDecimal amount,
                                        String description) {
}
