package com.banking.account.dto.request;

import com.banking.account.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class CreateAccountRequest {
@NotBlank(message = "Customer ID is required")
private String customerId;
@NotNull(message = "Account type is required")
private AccountType accountType;

@NotBlank(message = "Currency is required")
private String currency;

private String accountName;
private String description;

@PositiveOrZero(message = "Initial balance must be positive or zero")
private BigDecimal initialBalance = BigDecimal.ZERO;

@PositiveOrZero(message = "Overdraft limit must be positive or zero")
private BigDecimal overdraftLimit = BigDecimal.ZERO;

private List<String> jointHolders;

@Positive(message = "Daily withdrawal limit must be positive")
private BigDecimal dailyWithdrawalLimit;

@Positive(message = "Daily transfer limit must be positive")
private BigDecimal dailyTransferLimit;
}
