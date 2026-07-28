package com.banking.payment.controller;

import com.banking.payment.dto.CreatePaymentRequest;
import com.banking.payment.dto.response.PaymentOrderResponse;
import com.banking.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
        private final PaymentService paymentService;
        @PostMapping("/create-payment-order")
        public ResponseEntity<PaymentOrderResponse> createOrder(@Valid
                                                             @RequestBody CreatePaymentRequest createPaymentRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPaymentOrder(createPaymentRequest));
    }

    public void paymentWebHook(@RequestBody HashMap<String,Object> webhookRequest){
            paymentService.handlePaymentWebhook(webhookRequest);
    }


}
