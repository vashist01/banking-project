package com.ai.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class AccountBalanceResponse {
    private String accountNumber;

    private String accountType;

    private BigDecimal availableBalance;

    private BigDecimal ledgerBalance;

    private String currency;
}
