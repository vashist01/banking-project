package com.banking.transaction.dto.response;

import com.banking.transaction.enums.TransactionStatusEnum;
import com.banking.transaction.enums.TransactionTypeEnum;
import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransactionResponse(

    long transactionId,
    String senderAccountNumber,
    String receiverAccountNumber,
    BigDecimal amount,
    TransactionTypeEnum transactionType,
    TransactionStatusEnum transactionStatus,
    String description,
    String failureReason,
    String referenceNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt){
}
