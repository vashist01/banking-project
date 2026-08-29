package com.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransactionResponse(

    String transactionId,
    String senderAccountNumber,
    String receiverAccountNumber,
    BigDecimal amount,
    String transactionType,
    String transactionStatus,
    String description,
    String failureReason,
    String referenceNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt){
}