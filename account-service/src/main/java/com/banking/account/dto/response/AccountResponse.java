package com.banking.account.dto.response;

import com.banking.account.entity.AccountLimits;
import com.banking.account.enums.AccountStatus;
import com.banking.account.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private String id;
    private String accountNumber;
    private String customerId;
    private AccountType accountType;
    private AccountStatus status;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private BigDecimal holdBalance;
    private BigDecimal overdraftLimit;
    private String currency;
    private String accountName;
    private boolean frozen;
    private LocalDateTime openedDate;
    private List<String> jointHolders;
    private AccountLimits limits;
    private LocalDateTime createdAt;
}
