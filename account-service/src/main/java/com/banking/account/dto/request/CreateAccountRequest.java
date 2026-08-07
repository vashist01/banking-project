    package com.banking.account.dto.request;

    import com.banking.account.enums.AccountType;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Positive;
    import jakarta.validation.constraints.PositiveOrZero;
    import java.math.BigDecimal;
    import java.util.List;
    import lombok.Data;
    import tools.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
    import tools.jackson.databind.annotation.JsonNaming;

    @Data
    @JsonNaming(SnakeCaseStrategy.class)
    public class CreateAccountRequest {
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

    private String email;
    }
