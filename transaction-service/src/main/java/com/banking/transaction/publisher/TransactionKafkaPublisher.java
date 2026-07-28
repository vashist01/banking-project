package com.banking.transaction.publisher;

import com.banking.transaction.entity.Transaction;
import com.banking.transaction.entity.TransactionOutBox;
import com.banking.transaction.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionKafkaPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    private final String TRANSACTION_INITIATE_TOPIC = "transaction.initiated";
    private final String TRANSACTION_REFUND_TOPIC = "transaction.refund";
    private static final String FRAUD_DEDUCT_TOPIC ="fraud.deducted" ;
    private static final String TRANSACTION_COMPLETED_TOPIC = "transaction.completed";
    public CompletableFuture<SendResult<String, Object>> initiateTransactionEvent(TransactionOutBox transactionOutBox) {
        try{
            log.info("info: Transaction event is processing ...");
            TransactionInitiatedEvent transactionInitiatedEvent = TransactionInitiatedEvent.
                    builder().transactionId(String.valueOf(transactionOutBox.getTransactionId()))
                    .description(Optional.ofNullable(transactionOutBox.getDescription()).orElse("No Description"))
                    .receiverAccountNumber(transactionOutBox.getReceiverAccountNumber()).
                    amount(transactionOutBox.getAmount()).
                    senderAccountNumber(transactionOutBox.getSenderAccountNumber())
                    .build();
            return kafkaTemplate.send(TRANSACTION_INITIATE_TOPIC,
                    String.valueOf(transactionInitiatedEvent.getTransactionId()),
                    transactionInitiatedEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void sendTransactionRefundEvent(Transaction transaction, String reason) {
        String transactionId = String.valueOf(transaction.getId());
        TransactionDetails details = TransactionDetails.builder()
                .transactionId(Long.valueOf(transactionId))
                .accountNumber(transaction.getSenderAccountNumber())
                .amount(transaction.getAmount())
                .reason(reason)
                .build();
        TransactionRefundEvent transactionRefundEvent = TransactionRefundEvent.builder().
                transactionDetails(details).build();
        kafkaTemplate.send(TRANSACTION_REFUND_TOPIC,transactionId,transactionRefundEvent);
    }

    public void fraudDeductEvent(Transaction transaction,String reason){
        TransactionDetails details = TransactionDetails.builder()
                .transactionId(Long.valueOf(transaction.getId()))
                .accountNumber(transaction.getSenderAccountNumber())
                .reason(reason)
                .build();
        kafkaTemplate.send(FRAUD_DEDUCT_TOPIC, String.valueOf(transaction.getId()),details);
    }

    public void sendTransactionCompletedEvent(Transaction transaction) {
        String transactionId = String.valueOf(transaction.getId());
        TransactionCompletedEvent transactionCompletedEvent = TransactionCompletedEvent
                .builder().transactionId(transactionId).senderAccountNumber(transaction.getSenderAccountNumber()).
                receiverAccountNumber(transaction.getReceiverAccountNumber()).
                description(Optional.ofNullable(transaction.getDescription()).orElse(""))
        .build();
        kafkaTemplate.send(TRANSACTION_COMPLETED_TOPIC,transactionId,transactionCompletedEvent);
    }
}
