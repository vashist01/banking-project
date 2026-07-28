package com.banking.transaction.publisher;

import com.banking.transaction.event.OtpVerificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpVerificationEventPublisher {
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final String TRANSACTION_OTP_GENERATED_TOPIC = "transaction.otp.generated";
    public void publishOtpVerificationEvent(String transactionId, OtpVerificationEvent otpVerificationEvent) {
        kafkaTemplate.send(TRANSACTION_OTP_GENERATED_TOPIC,transactionId,otpVerificationEvent);
    }
}
