package com.banking.transaction.event;

import lombok.Builder;

@Builder
public record TransactionRefundEvent(TransactionDetails transactionDetails) implements TransactionEvent{
}
