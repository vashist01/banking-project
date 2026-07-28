package com.banking.transaction.event;

import lombok.Builder;

@Builder
public record OtpVerificationEvent(
        TransactionDetails transactionDetails,
          String otp ) implements TransactionEvent{


}
