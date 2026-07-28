package com.banking.transaction.event;

public sealed interface TransactionEvent permits OtpVerificationEvent,TransactionRefundEvent {
}
