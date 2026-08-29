package com.banking.account.service;

import com.banking.account.client.CustomerClient;
import com.banking.account.dto.request.CreateAccountRequest;
import com.banking.account.dto.request.FreezeRequest;
import com.banking.account.dto.response.AccountResponse;
import com.banking.account.entity.Account;
import com.banking.account.enums.AccountStatus;
import com.banking.account.enums.AccountType;
import com.banking.account.event.AccountCreatedEvent;
import com.banking.account.exception.AccountValidationException;
import com.banking.account.exception.DuplicateAccountException;
import com.banking.account.repository.AccountRepository;
import com.banking.account.repository.projection.BalanceProjection;
import com.banking.account.validator.AccountValidator;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private static final String ACCOUNT_NOT_FOUND = "Account not found.";
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountValidator accountValidator;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String,Object> redisTemplate;
    private final CustomerClient client;
    private final String DAILY_TRANSFER_LIMIT_KEY="dailyTransferLimitKey:";
    @Transactional
    @Retry(name = "customer-service",fallbackMethod = "customerFallback")
    @CircuitBreaker(name = "customer-service", fallbackMethod = "customerFallback")
    @Bulkhead(name = "customer-service",type = Type.SEMAPHORE,fallbackMethod = "customerFallback")
    public  AccountResponse createAccount(CreateAccountRequest createAccountRequest) {
        log.info("create account for : {}",createAccountRequest.getCustomerId());
      accountValidator.validateAccountRequest(createAccountRequest);
      String customerId = client.getCustomerId(createAccountRequest.getEmail());
        try{
            if (createAccountRequest.getAccountType() == AccountType.SAVINGS){
                accountRepository.findByCustomerIdAndAccountType(createAccountRequest.getCustomerId(),
                        AccountType.SAVINGS).ifPresent(account ->  {
                    throw new DuplicateAccountException("Saving Account is already exist by customerId",
                            String.valueOf(HttpStatus.ALREADY_REPORTED.value()));
                });
            }
            String accountNumber= accountNumberGenerator.generateAccountNumber(createAccountRequest.getAccountType(),
                    createAccountRequest.getCustomerId());
            Account account = convertToEntity(createAccountRequest);
            account.setAccountNumber(accountNumber);
            account.setCustomerId(createAccountRequest.getCustomerId());
            account.setStatus(AccountStatus.ACTIVE);
            account.setActive(true);
            account.setOpenedDate(LocalDateTime.now());
            account.setBalance(BigDecimal.ZERO);
            account.setAvailableBalance(BigDecimal.ZERO);
            account.setHoldBalance(BigDecimal.ZERO);
            account.setFrozen(false);

            if(createAccountRequest.getDailyWithdrawalLimit() == null){
                account.setDailyWithdrawalLimit(getDefaultWithdrawalLimit(createAccountRequest.getAccountType()));
            }if(createAccountRequest.getDailyTransferLimit() == null ){
                BigDecimal dailyTransferLimit = getDefaultTransferLimit(createAccountRequest.getAccountType());
                account.setDailyTransferLimit(dailyTransferLimit);
                redisTemplate.opsForValue().set(DAILY_TRANSFER_LIMIT_KEY+accountNumber,dailyTransferLimit);
            }

            if(createAccountRequest.getInitialBalance()!=null &&
                    createAccountRequest.getInitialBalance().compareTo(BigDecimal.ZERO) > 0){
                account.deposit(createAccountRequest.getInitialBalance());
            }
            Account savedAccount = accountRepository.save(account);
            log.info("Account created successfully: {}", savedAccount.getAccountNumber());
            sendNotification("account-event",savedAccount);
            return mapToAccountResponse(account);
        } catch (Exception e) {
             log.error("Error:Failed to create customer account {}",e.getMessage());
             throw new AccountValidationException("Internal Server is down please try after some time",
                     String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
        }

    }

    private void sendNotification(String kafkaTopic, Account savedAccount) {
        AccountCreatedEvent event = AccountCreatedEvent.builder()
                .accountId(savedAccount.getId())
                .accountNumber(savedAccount.getAccountNumber())
                .customerId(savedAccount.getCustomerId())
                .accountType(savedAccount.getAccountType().name())
                .initialBalance(savedAccount.getBalance())
                .currency(savedAccount.getCurrency())
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send(kafkaTopic,savedAccount.getId(),event);
        log.info("Published AccountCreatedEvent for account: {}", savedAccount.getId());
    }

  public AccountResponse customerFallback(
      CreateAccountRequest createAccountRequest,
      Exception ex) {

    log.error("Fallback Called", ex);

    throw new AccountValidationException(
        "Customer Service is unavailable",
        String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
  }

    private BigDecimal getDefaultTransferLimit(AccountType accountType) {
        return switch (accountType) {
            case CHECKING -> BigDecimal.valueOf(25000);
            case BUSINESS -> BigDecimal.valueOf(100000);
            default -> BigDecimal.valueOf(10000);
        };
    }

    private BigDecimal getDefaultWithdrawalLimit(AccountType accountType) {
       return switch (accountType){
            case CHECKING -> BigDecimal.valueOf(100000);
            case BUSINESS -> BigDecimal.valueOf(500000);
            default -> BigDecimal.valueOf(5000);
        };
    }



    private Account convertToEntity(CreateAccountRequest createAccountRequest) {
        return Account.builder().accountType(createAccountRequest.getAccountType())
                .accountName(createAccountRequest.getAccountName())
                .build();
    }


    public AccountResponse getAccount(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber).
                            orElseThrow(() -> new RuntimeException(ACCOUNT_NOT_FOUND));
        return mapToAccountResponse(account);
    }

    private AccountResponse mapToAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .customerId(account.getCustomerId())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .balance(account.getBalance())
                .availableBalance(account.getAvailableBalance())
                .holdBalance(account.getHoldBalance())
                .overdraftLimit(account.getOverdraftLimit())
                .currency(account.getCurrency())
                .accountName(account.getAccountName())
                .frozen(account.isFrozen())
                .openedDate(account.getOpenedDate())
                .jointHolders(account.getJointHolders())
                .limits(account.getLimits())
                .createdAt(account.getCreatedAt())
                .build();
    }


    public BigDecimal getBalance(String accountNumber) {
        BalanceProjection balanceProjection = accountRepository.findBalanceProjectionByAccountNumber(accountNumber);
        return Optional.ofNullable(balanceProjection).map(BalanceProjection::getBalance).orElse(BigDecimal.ZERO);
    }

    @Transactional
    public void blockAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException(ACCOUNT_NOT_FOUND));
        if(account.getStatus() == AccountStatus.ACTIVE){
            account.setStatus(AccountStatus.BLOCKED);
        }
    }
    @Transactional
    public void deductBalance(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException(ACCOUNT_NOT_FOUND));
        if(account.getStatus() == AccountStatus.ACTIVE && account.getBalance().compareTo(amount)  < 0){
            throw new RuntimeException("Insufficient balance");
        }
        BigDecimal balance = account.getBalance();
        BigDecimal remainingAmount = balance.subtract(amount);
        account.setBalance(remainingAmount);

    }

    @Transactional
    public void creditBalance(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException(ACCOUNT_NOT_FOUND));
        if(account.getStatus().equals(AccountStatus.ACTIVE)){
            account.setBalance(account.getBalance().add(amount));
        }
    }

    @CachePut(value = "account",key = "#request.accountNumber")
    @Transactional
    public AccountResponse freezeAccount(FreezeRequest request) {
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(request.getAccountNumber());
        accountOptional.ifPresentOrElse(account -> {
            if(account.getStatus() == AccountStatus.CLOSED){
                throw new AccountValidationException("Account is closed", "");
            }if(account.getStatus() == AccountStatus.FROZEN){
                throw new AccountValidationException("Account is already freezed", "");
            }
            account.setStatus(AccountStatus.FROZEN);
            account.setFrozen(true);
            accountRepository.save(account);

        },() ->{
            throw new AccountValidationException("Account not found", "");
        });

        return mapToAccountResponse(accountOptional.get());
    }
    @Transactional
    @CachePut(value = "account",key = "#accountNumber")
    public AccountResponse unfreezeAccount(String accountNumber) {
        log.info("Unfreezing account: {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> {
            throw  new AccountValidationException("Account not found with ID: " +accountNumber,"");
        });

        // check account if
        if(!account.isFrozen()){
            throw new IllegalStateException("Account is not frozen");
        }
        account.setFrozen(false);
        account.setFreezeReason(null);
        account.setFrozenAt(null);
        account.setFrozenBy(null);
        account.setStatus(AccountStatus.ACTIVE);

        return mapToAccountResponse(account);
    }

  public List<AccountResponse> getAllAccount() {
    List<Account> accountList = accountRepository.findAll();
    if(accountList.isEmpty()){
      return Collections.emptyList();
    }
    return accountList.stream().map(this::mapToAccountResponse).toList();
  }
}
