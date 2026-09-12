package com.banking.fraud.detection.event;
import java.math.BigDecimal;

public record TransactionInitiatedEvent (
      String eventId,
      String transactionId,
      String senderAccountNumber,
      String receiverAccountNumber,
      BigDecimal amount,
      String description){
}