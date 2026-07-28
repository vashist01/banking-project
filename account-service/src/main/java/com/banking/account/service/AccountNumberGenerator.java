package com.banking.account.service;

import com.banking.account.enums.AccountType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@Slf4j
public class AccountNumberGenerator {
    
    /**
     * Generates a unique account number based on account type and customer ID
     * Format: [TYPE_CODE][YEAR][MONTH][DAY][SEQUENCE]
     * Example: SV2024111500001 (Savings account created on Nov 15, 2024)
     */
    public String generateAccountNumber(AccountType accountType, String customerId) {
        // 1. Get account type code
        String typeCode = getAccountTypeCode(accountType);
        
        // 2. Get date component
        LocalDateTime now = LocalDateTime.now();
        String dateComponent = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 3. Generate sequence (using UUID for uniqueness)
        String sequence = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        
        // 4. Add customer ID suffix (last 4 digits of customer ID)
        String customerSuffix = customerId.replace("-", "");
        customerSuffix = customerSuffix.substring(
            Math.max(0, customerSuffix.length() - 4)
        );
        
        // Combine all components
        String accountNumber = String.format("%s%s%s%s", 
            typeCode, 
            dateComponent, 
            sequence, 
            customerSuffix
        );
        
        log.info("Generated account number: {} for customer: {}, type: {}", 
                 accountNumber, customerId, accountType);
        
        return accountNumber;
    }
    
    private String getAccountTypeCode(AccountType accountType) {
        return switch (accountType) {
            case SAVINGS -> "SV";
            case CURRENT -> "CR";
            case CHECKING -> "CH";
            case BUSINESS -> "BS";
            case FIXED_DEPOSIT -> "FD";
            case LOAN -> "LN";
            case CREDIT_CARD -> "CC";
        };
    }
}