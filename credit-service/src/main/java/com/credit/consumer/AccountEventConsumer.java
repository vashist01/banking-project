package com.credit.consumer;

import com.credit.service.CreditAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class AccountEventConsumer {
    private final CreditAccountService creditAccountService;
    @KafkaListener(topics = "credit-account",groupId = "account-service-group")
    public void  accountEvent(@Payload Map<String,Object> payload){
          creditAccountEvent(payload);
    }

  private void creditAccountEvent(Map<String, Object> payload) {
    creditAccountService.creditAmount(payload);
  }

  @KafkaListener(topics = "credit-account.DLT",groupId = "account-service-group")
  public void  dlqEvent(@Payload Map<String,Object> payload){
    creditAccountEvent(payload);
  }

}
