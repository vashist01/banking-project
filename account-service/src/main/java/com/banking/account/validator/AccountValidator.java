package com.banking.account.validator;

import com.banking.account.dto.request.CreateAccountRequest;
import com.banking.account.entity.Account;
import com.banking.account.enums.AccountStatus;
import com.banking.account.exception.AccountValidationException;
import com.banking.account.exception.TransferValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AccountValidator {
    String statusCode = String.valueOf(HttpStatus.BAD_REQUEST.value());

    public void validateAccountRequest(CreateAccountRequest request) {

        // 2. Validate account type
        if (request.getAccountType() == null) {
            throw new AccountValidationException("Account type is required",statusCode);
        }

        // 3. Validate currency
        if (request.getCurrency() == null || request.getCurrency().isEmpty()) {
            throw new AccountValidationException("Currency is required",statusCode);
        }

        // 4. Validate initial balance
        if (request.getInitialBalance() != null &&
                request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountValidationException("Initial balance cannot be negative",statusCode);
        }

        // 5. Validate overdraft limit
        if (request.getOverdraftLimit() != null &&
                request.getOverdraftLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountValidationException("Overdraft limit cannot be negative",statusCode);
        }

        // 6. Validate daily limits
        if (request.getDailyWithdrawalLimit() != null &&
                request.getDailyWithdrawalLimit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountValidationException("Daily withdrawal limit must be positive",statusCode);
        }

        if (request.getDailyTransferLimit() != null &&
                request.getDailyTransferLimit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountValidationException("Daily transfer limit must be positive",statusCode);
        }
    }

    public void validateAccountTransferRequest(String senderAccountNumber, String receiverAccountNumber,
                                               BigDecimal amount, String reference) {
        if(senderAccountNumber.equals(receiverAccountNumber)){
            throw new TransferValidationException("Cannot transfer to the same account");
        }if(amount == null || amount.compareTo(BigDecimal.ZERO)<=0){
            throw new TransferValidationException("Insufficient Balance");
        }if(amount.compareTo(new BigDecimal(25000l))>0){
            throw new TransferValidationException("Transfer amount exceeds maximum limit of 25,000.00");
        }
    }

    public void validateUserAccount(Account senderAccount, Account receiverAccount) {

       //sender account
        if(!senderAccount.isActive()){
            throw new TransferValidationException("Source account is not active");
        }if(!senderAccount.isFrozen()){
            throw new TransferValidationException("Source account is frozen");
        }if(senderAccount.getStatus() != AccountStatus.ACTIVE){
            throw new TransferValidationException("Source account status is: " + senderAccount.getStatus());
        }

        // receiver account
        if(!receiverAccount.isActive()){
            throw new TransferValidationException("Source account is not active");
        }if(!receiverAccount.isFrozen()){
            throw new TransferValidationException("Source account is frozen");
        }if(receiverAccount.getStatus() != AccountStatus.ACTIVE){
            throw new TransferValidationException("Source account status is: " + senderAccount.getStatus());
        }
    }
}
