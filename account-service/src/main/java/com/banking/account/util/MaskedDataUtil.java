package com.banking.account.util;

import org.springframework.stereotype.Component;

@Component
public class MaskedDataUtil {
    public String getMaskAccountNumber(String accountNumber) {
        String maskedAccountNumber = "*".repeat(accountNumber.length() -4) +
                accountNumber.substring(accountNumber.length() - 4);
        return maskedAccountNumber;
    }
}
