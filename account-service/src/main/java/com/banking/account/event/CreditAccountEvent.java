package com.banking.account.event;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditAccountEvent {

    private String transactionId;

    private String senderAccountNumber;

    private Long senderCustomerId;

    private String receiverAccountNumber;

    private Long receiverCustomerId;

    private BigDecimal amount;

    private String currency;

    private String remarks;

    private LocalDateTime transactionTime;