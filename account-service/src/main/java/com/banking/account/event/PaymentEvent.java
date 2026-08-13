package com.banking.account.event;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private String paymentId;

    private String accountId;

    private BigDecimal amount;

    private String status;

}