package com.banking.payment.pattern;

import com.banking.payment.entity.Payment;

public interface PaymentStrategy {
 
    void pay(Payment payment);
}