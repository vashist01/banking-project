package com.banking.fraud.detection.consumer;

import com.banking.fraud.detection.event.TransactionInitiatedEvent;
import com.banking.fraud.detection.service.FraudDeductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FraudDeductionConsumer {
    private final FraudDeductionService fraudDeductionService;
    @KafkaListener(topics = "transaction.initiated")
    public void fraudDeductionEventConsumer(@Payload TransactionInitiatedEvent transactionInitiatedEvent){
      try{
          log.info("Received Transaction for fraud check:{}",transactionInitiatedEvent.transactionId());
          fraudDeductionService.checkTransaction(transactionInitiatedEvent);
      } catch (Exception e) {
        log.error("failed to consume fraudDeduction event",e.getMessage());
      }
    }
}
