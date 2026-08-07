package com.credit.service;

import com.credit.entity.Account;
import com.credit.enums.AccountStatus;
import com.credit.repository.AccountRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditAccountService {
    private AccountRepository accountRepository;
    @Transactional
    public void creditAmount(Map<String,Object> payload) {
      BigDecimal amount = (BigDecimal) payload.get("amount");

      String senderAccountNumber = payload.get("senderAccountNumber").toString();
      String receiverAccountNumber = (String) payload.get("receiverAccountNumber");
      String customerId = (String) payload.get("receiverCustomerId");

      Account account  = accountRepository.findByAccountNumberAndCustomerId(receiverAccountNumber,
          customerId).orElseThrow(()-> new RuntimeException("Account Not Found "));
      account.setAccountName(receiverAccountNumber);
      account.setCustomerId(customerId);
      if (account.getStatus() != AccountStatus.ACTIVE) {

        throw new RuntimeException("Account Not Active");
      }if (!account.getCurrency().equals("INR")) {

        throw new RuntimeException("Currency mismatch");
      }
        BigDecimal availableBalance = account.getAvailableBalance();
        BigDecimal newBalance = availableBalance.add(amount);
        account.getBalance().add(newBalance);
        account.getAvailableBalance().add(newBalance);
        account.setLastTransactionAt(LocalDateTime.now());

        accountRepository.save(account);
      }
}
