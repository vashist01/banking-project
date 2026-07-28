package com.banking.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CreatePaymentRequest {
    @NotBlank(message = "Account Number is required.")
    private String accountNumber;
    @NotBlank(message = "Amount is required.")
    @Positive(message = "Amount is required with positive amount.")
    private BigDecimal amount;

    private String description;
}
