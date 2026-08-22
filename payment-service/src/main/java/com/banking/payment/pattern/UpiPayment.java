package com.banking.payment.pattern;

import org.springframework.stereotype.Component;

import com.banking.payment.entity.Payment;
import com.banking.payment.enums.PaymentStatus;

@Component("UPI")
public class UpiPayment implements PaymentStrategy{

    @Override
    public void pay(Payment payment) { 
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
    }
    
}
