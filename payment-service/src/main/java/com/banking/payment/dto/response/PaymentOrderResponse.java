package com.banking.payment.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentOrderResponse(
        String paymentId,
        String razorpayOrderId,
        BigDecimal amount,
        String currency,
        String status,
        String razorpayKeyId

) {
}
