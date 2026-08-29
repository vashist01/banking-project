package com.banking.transaction.service;

import com.banking.transaction.client.TransactionServiceClient;
import com.banking.transaction.dto.request.TransactionRequest;
import com.banking.transaction.dto.response.TransactionResponse;
import com.banking.transaction.dto.response.TransactionSummaryResponse;
import com.banking.transaction.entity.Transaction;
import com.banking.transaction.entity.TransactionOutBox;
import com.banking.transaction.enums.TransactionStatusEnum;
import com.banking.transaction.enums.TransactionTypeEnum;
import com.banking.transaction.repository.TransactionOutBoxRepository;
import com.banking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionServiceClient transactionServiceClient;
    private final TransactionOutBoxRepository transactionOutBoxRepository;
    private final RedisTemplate redisTemplate;

    @Transactional
    public  TransactionResponse initiateTransaction(TransactionRequest transactionRequest) {
        // initiate balance for deduction
        transactionServiceClient.deductBalance(transactionRequest.senderAccountNumber(),transactionRequest.amount());

        Transaction transaction = Transaction.builder().senderAccountNumber(transactionRequest.senderAccountNumber())
                .receiverAccountNumber(transactionRequest.receiverAccountNumber()).
                transactionStatus(TransactionStatusEnum.PROCESSING).
            transactionType(TransactionTypeEnum.TRANSFER).amount(transactionRequest.amount())
                .description(transactionRequest.description()).referenceNumber(UUID.randomUUID().toString()).build();
        Transaction transaction1 = transactionRepository.save(transaction);

        // OutBoxPattern for fraud check
      TransactionOutBox transactionOutBox = getTransactionOutBox(
          transactionRequest, transaction1);
      transactionOutBoxRepository.save(transactionOutBox);
        return mapToTransactionResponse(transaction1);
    }

  private static TransactionOutBox getTransactionOutBox(TransactionRequest transactionRequest,
      Transaction transaction1) {
    TransactionOutBox transactionOutBox = new TransactionOutBox();
    transactionOutBox.setTransactionId(String.valueOf(transaction1.getId()));
    transactionOutBox.setTransactionStatus(TransactionStatusEnum.PROCESSING.name());
    transactionOutBox.setAmount(transaction1.getAmount());
    transactionOutBox.setSenderAccountNumber(transaction1.getSenderAccountNumber());
    transactionOutBox.setReceiverAccountNumber(transaction1.getReceiverAccountNumber());
    transactionOutBox.setReceiverAccountNumber(transaction1.getReceiverAccountNumber());
    transactionOutBox.setCustomerId(transactionRequest.customerId());
    return transactionOutBox;
  }

  private TransactionResponse mapToTransactionResponse(Transaction transaction1) {
        return TransactionResponse.builder().transactionId(transaction1.getId()).referenceNumber(transaction1.getReferenceNumber())
                .receiverAccountNumber(transaction1.getReceiverAccountNumber()).
                senderAccountNumber(transaction1.getSenderAccountNumber())
                .transactionStatus(transaction1.getTransactionStatus()).transactionType(transaction1.getTransactionType())
                .failureReason(Optional.ofNullable(transaction1.getFailureReason()).orElse(""))
                .createdAt(transaction1.getCreatedAt()).amount(transaction1.getAmount())
                .description(Optional.ofNullable(transaction1.getDescription()).orElse("No description added")).build();
    }

    public  TransactionResponse getTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("No Transaction Found."));
        return mapToTransactionResponse(transaction);
    }

    public List<TransactionResponse> getTransactionHistory(String senderAccountNumber) {
        List<Transaction> transactionList = transactionRepository.
                findBySenderAccountNumberOrderByCreatedAtDesc(senderAccountNumber);
        if(CollectionUtils.isEmpty(transactionList)){
            throw new RuntimeException("Transaction not found By sender account number.");
        }
        return transactionList.stream().map(this::mapToTransactionResponse).toList();
    }

    @Transactional
    public TransactionResponse verifyOTP(String transactionId, String otp) {
        Transaction transaction = transactionRepository.findById( transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found."));
        String otpKey = "verification:otp"+transactionId;
        String storedOtp = redisTemplate.opsForValue().get(otpKey).toString();

        if(StringUtils.hasText(storedOtp)){
            log.warn("Otp Expired for Transaction: {}",storedOtp);
            compensateTransaction(transaction,"OTP expired - transaction cancelled and amount refunded");
            return mapToTransactionResponse(transaction);
        }if(!storedOtp.equals(otp)){
            log.warn("Wrong Otp: blocking account and refunded: {}",transactionId);
            redisTemplate.delete(otpKey);
             blockAccountAndCompensateTransaction(transaction,"Wrong otp entered: transaction cancelled. " +
                    "account blocked for security");
            return mapToTransactionResponse(transaction);
        }
        log.info("Otp Verified - complete transaction: {}",transactionId);
        redisTemplate.delete(otpKey);
         completeTransaction(transaction);
        return mapToTransactionResponse(transaction);
    }

    private void compensateTransaction(Transaction transaction, String reason) {
        log.warn("SAGA TRANSACTION : refunding: {} amount:{}",transaction.getSenderAccountNumber(),
                transaction.getAmount());
        // CREDIT MONEY BACK TO SENDER SYNCHRONOUSLY
        transactionServiceClient.creditBalance(transaction.getSenderAccountNumber(),transaction.getAmount());
        transaction.setTransactionStatus(TransactionStatusEnum.FLAGGED);
        transaction.setFailureReason(" - Saga Compensate executed. amount refunded at " + LocalDateTime.now());
        transactionRepository.save(transaction);

        // PUBLISH REFUND EVENT - Notification service will alert user
       // transactionKafkaPublisher.sendTransactionRefundEvent(transaction,reason);
    }

    private void blockAccountAndCompensateTransaction(Transaction transaction, String reason) {
        //transactionKafkaPublisher.fraudDeductEvent(transaction,reason);
        //SAGA COMPENSATE - REFUND TRANSACTION
        completeTransaction(transaction);
    }

    private void completeTransaction(Transaction transaction) {
        transaction.setTransactionStatus(TransactionStatusEnum.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        // transactionKafkaPublisher.sendTransactionCompletedEvent(transaction);
    }


    public void processCleanTransactionResult(String transactionId) {
        Transaction transaction = transactionRepository.findById( transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found."));

        if(transaction.getTransactionStatus()!=null && transaction.getTransactionStatus() == TransactionStatusEnum.PROCESSING){
            log.info("Transaction is processing : {}",transactionId);
            return;
        }
        completeTransaction(transaction);
    }

    public TransactionSummaryResponse getTransactionSummary(String accountNumber, LocalDateTime startDate,
                                                            LocalDateTime endDate) {

        log.info("Generating transaction summary for account: {}", accountNumber);
        //List<Transaction> transactionList = transactionRepository.findByAccountNumber(accountNumber,startDate,endDate);
        return null;
    }
  @Transactional
  public void updateTransactionStatus(String transactionId, TransactionStatusEnum transactionStatusEnum) {
    Transaction transaction = transactionRepository.findByTransactionId(transactionId).orElseThrow(() ->
        new RuntimeException("Transaction Not Found by TransactionId"));
    transaction.setTransactionStatus(transactionStatusEnum);


  }
  @Cacheable(value = "transactions", key = "'all-transactions'")
  public List<TransactionResponse> getAllTransactions() {
     List<Transaction> transactionsList = transactionRepository.findAll();
     if(CollectionUtils.isEmpty(transactionsList)){
        return Collections.emptyList();
     }
     return transactionsList.stream().map(this::mapToTransactionResponse).toList(); 
  }
}
