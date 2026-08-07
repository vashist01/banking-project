package com.banking.fraud.detection.service;

import com.banking.fraud.detection.client.AccountServiceClient;
import com.banking.fraud.detection.config.PropertyConfig;
import com.banking.fraud.detection.dto.FraudCheckResult;
import com.banking.fraud.detection.event.TransactionInitiatedEvent;
import com.banking.fraud.detection.event.VerificationOtpEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudDeductionService {
    private static final String VERIFICATION_REQUIRED_TOPIC = "verification.required";
    private static final String FRAUD_CHECK_CLEAN_TOPIC = "fraud.check.clean";
    private final AccountServiceClient accountServiceClient;
    private final PropertyConfig propertyConfig;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final RedisTemplate redisTemplate;

    public void checkTransaction(TransactionInitiatedEvent transactionInitiatedEvent) {
        String senderAccountNumber =transactionInitiatedEvent.senderAccountNumber();
        String transactionId = transactionInitiatedEvent.transactionId();
        BigDecimal amount = transactionInitiatedEvent.amount();
        // fetch real balance from account service
        BigDecimal senderBalance = accountServiceClient.getBalance(senderAccountNumber);

        FraudCheckResult fraudCheckResult = performFraudChecks(senderAccountNumber,amount,senderBalance);
        if(fraudCheckResult.fraud()){
            log.info("Suspicious activity detected -amount:{}"+
                    "reason{} - requesting OTP verification",amount,fraudCheckResult.reason());
            VerificationOtpEvent verificationOtpEvent = VerificationOtpEvent.builder().
            transactionId(transactionId).accountNumber(senderAccountNumber).amount(amount)
                    .reason(fraudCheckResult.reason())
                    .build();
            kafkaTemplate.send(VERIFICATION_REQUIRED_TOPIC,transactionId,verificationOtpEvent);
            log.info("VERIFICATION_REQUIRED_TOPIC: sent to traction-service to verified with otp");

        }else{
            VerificationOtpEvent verificationOtpEvent = VerificationOtpEvent.builder()
                    .reason(null).isFraud(false).transactionId(transactionId).build();
            kafkaTemplate.send(FRAUD_CHECK_CLEAN_TOPIC,transactionId,verificationOtpEvent);
        }
    }

    private FraudCheckResult performFraudChecks(String accountNumber, BigDecimal amount, BigDecimal senderBalance) {

        // pattern 1.velocity check
        try{
            if(isVelocityExpectedExceeded(accountNumber)){

                return new FraudCheckResult(true,"Too many Transaction in 60 seconds "
                        +" - exceeds 3x your average");
            }if(isAmountSuspicious(accountNumber,amount)){
                return new FraudCheckResult(true,"Unusual Transaction amount  "
                        +" - exceeds 3x your average");
            }if(senderBalance.compareTo(BigDecimal.ZERO)> 0 &&
                    isBalanceCheckFailed(senderBalance,amount)){
                return new FraudCheckResult(true,"Transaction exceed 90% of account balance");
            }
        }catch (Exception exception){
            log.error("Failed : PerformFraudChecks issue :{}",exception);
        }
          return new FraudCheckResult(true,null);
    }

    private boolean isVelocityExpectedExceeded(String accountNumber) {
        String key = "velocity_excepted"+accountNumber;
        Long count = redisTemplate.opsForValue().increment(key);
        if(count!=null && count ==1){
            redisTemplate.expire(key,60, TimeUnit.SECONDS);
        }
        log.info("velocity check - account: {}  count{}/{}",accountNumber,count,propertyConfig.getMaxCountPerMinute());
        return count !=null && count > propertyConfig.getMaxCountPerMinute();

    }


    private boolean isAmountSuspicious(String accountNumber, BigDecimal amount) {
        String key = "fraud:average_amount:" + accountNumber;

        Object value = redisTemplate.opsForValue().get(key);

        String averageStr = value != null ? value.toString() : null;
        if(!StringUtils.hasText(averageStr)){
            redisTemplate.opsForValue().set(key,amount.toString());
            return false;
        }

        BigDecimal aveAmount = new BigDecimal(averageStr);
        BigDecimal threshold = aveAmount.multiply(BigDecimal.valueOf(propertyConfig.getSuspiciousnessMultiplier()));
        // update running average
        BigDecimal newAvg = aveAmount.divide(BigDecimal.valueOf(2),2, RoundingMode.HALF_UP);
        redisTemplate.opsForValue().set(key,newAvg.toString());
        return amount.compareTo(threshold) > 0;
    }

    private boolean isBalanceCheckFailed(BigDecimal senderBalance, BigDecimal amount) {

        BigDecimal maxAllowed = senderBalance.multiply( BigDecimal.valueOf(Long.valueOf(propertyConfig.getMaxBalancePercentage())));
        return amount.compareTo(maxAllowed) >0;
    }

}
