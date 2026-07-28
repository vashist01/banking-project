package com.banking.account.event;



import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountCreatedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String accountId;

    private String accountNumber;

    private String customerId;

    private String accountType;

    private BigDecimal initialBalance;

    private String currency;

    private LocalDateTime timestamp;
}
