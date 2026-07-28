package com.banking.transaction.consumer;

import com.banking.transaction.entity.Transaction;
import com.banking.transaction.enums.TransactionStatusEnum;
import com.banking.transaction.event.OtpVerificationEvent;
import com.banking.transaction.event.TransactionDetails;
import com.banking.transaction.publisher.OtpVerificationEventPublisher;
import com.banking.transaction.repository.TransactionRepository;
import com.banking.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionEventConsumer {
    private final TransactionRepository transactionRepository;
    private final RedisTemplate redisTemplate;
    private final OtpVerificationEventPublisher otpVerificationEventPublisher;
    private final int OTP_VERIFICATION_TIME_LIMIT = 5;
    private final TransactionService transactionService;
    /**
     * consume verification.required
     * generate otp and ask to user to verify
     * @param payload
     */

    @KafkaListener(topics = "verification.required",
            properties = {
                    "spring.json.value.default.type=java.util.HashMap",
                    "spring.json.use.type.headers=false"  // Ignore type headers
            })
      void consumeVerificationEvent(@Payload Map<String, Object> payload){
        try{
            String transactionId = (String) payload.get("transactionId");
            String accountNumber = (String) payload.get("accountNumber");
            String reason  = (String) payload.get("reason");

            Transaction transaction = transactionRepository.findById(Long.valueOf(transactionId)).orElseThrow(() ->
                    new RuntimeException("Transaction not found."));

            if(transaction.getTransactionStatus() == TransactionStatusEnum.PROCESSING){
                log.warn("Transaction {} not PROCESSING -skipping",transactionId);
                return;

            }
            String otp = String.format("%6d",(int)(Math.random() * 900000)+100000);
            String otpKey = "verification:otp"+transactionId;

            // store otp in redis -expires in 5 minute
            redisTemplate.opsForValue().set(otpKey,otp,OTP_VERIFICATION_TIME_LIMIT, TimeUnit.MINUTES);
            transaction.setTransactionStatus(TransactionStatusEnum.PENDING_VERIFICATION);

            transactionRepository.save(transaction);
            TransactionDetails details = TransactionDetails.builder()
                    .transactionId(Long.valueOf(transactionId))
                    .accountNumber(accountNumber)
                    .amount(BigDecimal.valueOf((Long) payload.get("amount")))
                    .reason(reason)
                    .build();

            OtpVerificationEvent otpVerificationEvent = OtpVerificationEvent.builder()
                    .otp(otp).transactionDetails(details).build();
            otpVerificationEventPublisher.publishOtpVerificationEvent(transactionId,otpVerificationEvent);
        } catch (Exception e) {
            log.error("Error handling verification otp generation",e.getMessage());
        }
    }
    @KafkaListener(topics = "fraud.check.clean")
    public void consumeFraudCheckCleanResult(@Payload Map<String,Object> payload){
        try{
            Long transactionId = (Long) payload.get("transactionId");
            transactionService.processCleanTransactionResult(transactionId);
        }catch (Exception exception){
            log.error("Failed to process clean transaction: {}",exception.getMessage());
        }
    }
}
