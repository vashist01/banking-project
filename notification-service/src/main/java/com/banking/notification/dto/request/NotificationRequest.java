package com.banking.notification.dto.request;

import com.banking.notification.enums.NotificationChanel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private String recipient;
    private String subject;
    private String message;
    private String accountNumber;
    private String customerId;
    private String accountType;
    private BigDecimal initialBalance;
    private NotificationChanel channel;
}
