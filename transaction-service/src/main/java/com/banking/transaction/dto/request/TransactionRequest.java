package com.banking.transaction.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransactionRequest(
        @NotBlank(message = "Sender account number is required.")
        String senderAccountNumber,
        @NotBlank(message = "receiver account number is required.")
        String receiverAccountNumber,
        @NotNull(message = "Amount is required.")
        @Positive(message = "Amount should be greater then 0")
        BigDecimal amount,
        String description
) {
}
