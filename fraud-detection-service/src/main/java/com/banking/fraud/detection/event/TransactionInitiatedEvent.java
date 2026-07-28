package com.banking.fraud.detection.event;
import java.math.BigDecimal;

public record TransactionInitiatedEvent (
      String transactionId,
      String senderAccountNumber,
      String receiverAccountNumber,
      BigDecimal amount,
      String description){
}