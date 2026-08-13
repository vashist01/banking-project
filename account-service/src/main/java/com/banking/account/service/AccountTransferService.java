package com.banking.account.service;

import com.banking.account.dto.request.BulkTransferRequest;
import com.banking.account.dto.response.AccountBalanceResponse;
import com.banking.account.dto.response.TransferResponse;
import com.banking.account.entity.Account;
import com.banking.account.entity.OutBoxPattern;
import com.banking.account.exception.AccountValidationException;
import com.banking.account.exception.TransferValidationException;
import com.banking.account.repository.AccountRepository;
import com.banking.account.repository.OutBoxPatternRepository;
import com.banking.account.repository.projection.DailyTransferLimitProjection;
import com.banking.account.validator.AccountValidator;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountTransferService {

  private static final String TRANSFER_LOCK_PREFIX = "transfer:lock:";
  private final AccountRepository accountRepository;
  private final RedisTemplate<String, Object> redisTemplate;
  private final OutBoxPatternRepository outBoxPatternRepository;
  private final AccountValidator accountValidator;
  private final String DAILY_TRANSFER_LIMIT_KEY = "dailyTransferLimitKey:";

  @Transactional
  @CachePut(value = "account", key = "#account.accountNumber")
  public TransferResponse transfer(String senderAccountNumber, String receiverAccountNumber,
      BigDecimal amount, String transactionId, String senderCustomerId, String receiverCustomerId) {
    log.info("Processing transfer from {} to {} amount: {} reference: {}", senderAccountNumber,
        receiverAccountNumber, amount, transactionId);
    Account senderAccount = accountRepository.findByAccountNumber(senderAccountNumber).orElseThrow(
        () -> new AccountValidationException("Account not found By AccountNumber", ""));

    Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber)
        .orElseThrow(
            () -> new AccountValidationException("Account not found By AccountNumber", ""));

    accountValidator.validateAccountTransferRequest(senderAccountNumber, receiverAccountNumber,
        amount, transactionId);
    checkDailyTransferLimit(senderAccountNumber, amount);
    String transferId = UUID.randomUUID().toString();
    acquireTransferLocks(senderAccountNumber, receiverAccountNumber, transferId);
    try {
      accountValidator.validateUserAccount(senderAccount, receiverAccount);
      //withdraw the amount from sender account
      BigDecimal transferFee = calculateTransferFee(senderAccountNumber, receiverAccountNumber,
          amount);
      BigDecimal totalDebit = amount.add(transferFee);
      BigDecimal maxWithDrawAmount = senderAccount.getAvailableBalance()
          .add(senderAccount.getOverdraftLimit());

      if (amount.compareTo(maxWithDrawAmount) > 0) {
        throw new TransferValidationException(
            String.format("Insufficient balance. Available: %s, Overdraft: %s, Requested: %s",
                senderAccount.getAvailableBalance(), senderAccount.getOverdraftLimit(), amount));
      }
      // withdraw amount
      BigDecimal balance = senderAccount.getBalance().subtract(totalDebit);
      BigDecimal availableBalance = senderAccount.getAvailableBalance().subtract(totalDebit);
      senderAccount.setBalance(balance);
      senderAccount.setAvailableBalance(availableBalance);
      senderAccount.setLastTransactionAt(LocalDateTime.now());
      accountRepository.save(senderAccount);

      OutBoxPattern outBoxPattern = new OutBoxPattern();
      outBoxPattern.setReceiverAccountNumber(receiverAccountNumber);
      outBoxPattern.setSenderAccountNumber(senderAccountNumber);
      outBoxPattern.setTransactionId(transactionId);
      outBoxPattern.setCurrency("INR");
      outBoxPattern.setReceiverCustomerId("12354");
      outBoxPattern.setSenderCustomerId(senderCustomerId);
      outBoxPattern.setReceiverCustomerId(receiverCustomerId);
      outBoxPattern.setEventStatus("EVENT-PROCESSING");
      outBoxPatternRepository.save(outBoxPattern);

    } catch (Exception exception) {
      releaseTransferLock(senderAccountNumber, receiverAccountNumber, transferId);
    }

    return mapTransferResponse(senderAccount, transferId, receiverAccount);
  }

  private void releaseTransferLock(String senderAccountNumber, String receiverAccountNumber,
      String transferId) {
    String sourceAccountNumberLock, destinationAccountNumberLock;
    if (senderAccountNumber.equals(receiverAccountNumber)) {
      sourceAccountNumberLock = senderAccountNumber;
      destinationAccountNumberLock = receiverAccountNumber;
    } else {
      destinationAccountNumberLock = senderAccountNumber;
      sourceAccountNumberLock = receiverAccountNumber;
    }
    releaseLock(senderAccountNumber, transferId);
    if (!sourceAccountNumberLock.equals(destinationAccountNumberLock)) {
      releaseLock(receiverAccountNumber, transferId);
    }
  }

  private void releaseLock(String accountNumber, String transferId) {
    String lockKey = TRANSFER_LOCK_PREFIX + accountNumber;
    String currentOwner = (String) redisTemplate.opsForValue().get(lockKey);

    if (transferId.equals(currentOwner)) {
      redisTemplate.delete(lockKey);
    }
  }


  private TransferResponse mapTransferResponse(Account senderAccount, String transferId,
      Account receiverAccount) {
    return TransferResponse.builder().transferId(transferId).amount(senderAccount.getBalance())
        .sourceNewBalance(senderAccount.getBalance())
        .destinationNewBalance(receiverAccount.getBalance())
        .sourceAccountId(senderAccount.getAccountNumber())
        .destinationAccountId(receiverAccount.getAccountNumber())
        .currency(senderAccount.getCurrency()).build();
  }


  private BigDecimal calculateTransferFee(String senderAccountNumber, String receiverAccountNumber,
      BigDecimal amount) {
    boolean internalTransfer =
        senderAccountNumber.startsWith("SV") && receiverAccountNumber.startsWith("SV");
    if (internalTransfer) {
      boolean isPremiumCustomer = false; // call here customer and find
      if (isPremiumCustomer) {
        return BigDecimal.ZERO;
      }
      // Regular customers pay 0.1% for internal transfers
      return amount.multiply(new BigDecimal("0.001"));
    } else {
      if (amount.compareTo(new BigDecimal("1000")) > 0) {
        // Large transfers: 0.5% fee
        return amount.multiply(new BigDecimal("0.005"));
      } else {
        // Small transfers: flat fee of $1.00
        return new BigDecimal("1.00");
      }
    }
  }


  private void acquireTransferLocks(String senderAccountNumber, String receiverAccountNumber,
      String transferId) {
    String lock1, lock2;
    if (senderAccountNumber.compareTo(receiverAccountNumber) < 0) {
      lock1 = senderAccountNumber;
      lock2 = receiverAccountNumber;
    } else {
      lock1 = receiverAccountNumber;
      lock2 = senderAccountNumber;
    }
    acquireLock(lock1, transferId);
    if (!lock1.equals(lock2)) {
      acquireLock(receiverAccountNumber, transferId);
    }

  }

  private void acquireLock(String accountNumber, String transferId) {
    String key = TRANSFER_LOCK_PREFIX + transferId;
    Boolean acquireLock = redisTemplate.opsForValue()
        .setIfAbsent(accountNumber, transferId, Duration.ofDays(1));
    if (Boolean.FALSE.equals(acquireLock)) {
      String currentOwner = redisTemplate.opsForValue().get(key).toString();
      throw new TransferValidationException(
          "Account " + accountNumber + " is currently being modified by another transfer: "
              + currentOwner + ". Please try again.");
    }
  }

  private void checkDailyTransferLimit(String senderAccountNumber, BigDecimal amount) {
    BigDecimal dailyTransferLimit = (BigDecimal) redisTemplate.opsForValue()
        .get(DAILY_TRANSFER_LIMIT_KEY + senderAccountNumber);
    if (dailyTransferLimit == null) {
      DailyTransferLimitProjection dailyTransferLimitProjection = accountRepository.findDailyTransferLimitByAccountNumber(
          senderAccountNumber);
      BigDecimal dailyTransferLimitDB = dailyTransferLimitProjection.getDailyTransferLimit();
      if (dailyTransferLimitDB == null) {
        dailyTransferLimit = BigDecimal.ZERO;
      }
      if (dailyTransferLimit.add(amount).compareTo(dailyTransferLimit) > 0) {
        throw new TransferValidationException(
            "Daily transfer limit exceeded. Limit: " + dailyTransferLimit + ", Used: "
                + dailyTransferLimit + ", Requested: " + amount);
      }
    }
  }

  public List<TransferResponse> bulkTransfer(BulkTransferRequest request) {
    return List.of();
  }

  public AccountBalanceResponse holdAmount(String id, BigDecimal amount, String reference) {
    return null;
  }

  public AccountBalanceResponse releaseHold(String id, BigDecimal amount, String reference) {
    return null;
  }
}
