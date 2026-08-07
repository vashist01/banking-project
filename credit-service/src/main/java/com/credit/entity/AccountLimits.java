package com.credit.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.math.BigDecimal;

@Embeddable
@Data
public class AccountLimits {
    private BigDecimal maxDepositPerDay;
    private BigDecimal maxWithdrawalPerDay;
    private BigDecimal maxTransferPerDay;
    private Integer maxTransactionsPerDay;
    private BigDecimal minBalanceRequired;
}
