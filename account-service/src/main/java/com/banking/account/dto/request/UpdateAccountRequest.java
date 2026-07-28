package com.banking.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequest {
    private String accountName;
    private String description;
    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal dailyTransferLimit;
    private BigDecimal overdraftLimit;
    private List<String> jointHolders;
}