package com.banking.account.consumer;

import com.banking.account.service.AccountService;
import com.banking.account.util.MaskedDataUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.math.BigDecimal;
import java.util.Map;
@Slf4j
@RequiredArgsConstructor
public class AccountEventConsumer {
    private final AccountService accountService;
    private final MaskedDataUtil maskedDataUtil;
    @KafkaListener(topics = "transaction.completed")
    public void consumeTransactionCompleted(@Payload Map<String,Object> payload){
        try{
            String receiverAmount = (String)payload.get("receiverAmount");
            BigDecimal amount = (BigDecimal) payload.get("amount");
            log.info("Crediting amount: {} amount : {}",receiverAmount,amount);
            accountService.creditBalance(receiverAmount,amount);

        }catch (Exception exception){
            log.error("Error: Crediting account :{}", exception.getMessage());
        }
    }

    @KafkaListener(topics = "fraud.deducted")
    public void fraudDeduction(@Payload Map<String,Object> payload){
        String accountNumber = (String) payload.get("accountNumber");
        log.info("masked: AccountNumber is : {}",maskedDataUtil.getMaskAccountNumber(accountNumber));
        accountService.blockAccount(accountNumber);
    }
}
