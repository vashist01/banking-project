package com.banking.fraud.detection.consumer;

import com.banking.fraud.detection.event.TransactionInitiatedEvent;
import com.banking.fraud.detection.service.FraudDeductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
@Slf4j
@RequiredArgsConstructor
@Component
public class FraudDeductionConsumer {
    private final FraudDeductionService fraudDeductionService;
    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(topics = "transaction.initiated")
    public void fraudDeductionEventConsumer(@Payload TransactionInitiatedEvent transactionInitiatedEvent){ 
          log.info("Received Transaction for fraud check:{}",transactionInitiatedEvent.transactionId());
          fraudDeductionService.checkTransaction(transactionInitiatedEvent); 

           log.info(
                "Fraud check completed successfully for transaction: {}",
                transactionInitiatedEvent.transactionId()
        );
    }

  
    @DltHandler
    public void handleDlt(@Payload TransactionInitiatedEvent transactionInitiatedEvent){
         log.error(
                "Transaction moved to DLT. transactionId={}",
                transactionInitiatedEvent.transactionId()
        );
         fraudDeductionService.checkTransaction(transactionInitiatedEvent); 
         log.info(
                "Fraud check completed successfully for transaction: {}",
                transactionInitiatedEvent.transactionId()
        );
    }

}
