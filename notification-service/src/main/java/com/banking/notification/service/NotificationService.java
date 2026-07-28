package com.banking.notification.service;

import com.banking.notification.dto.request.NotificationRequest;
import com.banking.notification.pattern.NotificationFactory;
import com.banking.notification.pattern.NotificationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationFactory notificationFactory;

    public void sendNotificationAlert(Map<String, Object> payload) {
        // Send email/SMS notification about account changes
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setMessage("Your Account is successfully created please check your account balance ");
        notificationRequest.setAccountNumber((String) payload.get("accountNumber"));
        notificationRequest.setSubject("Account creation");
        notificationRequest.setInitialBalance((BigDecimal) payload.get("initialBalance"));
        for(NotificationStrategy notificationStrategy: notificationFactory.getAllStrategies()){
            notificationStrategy.send(notificationRequest);
        }
    }
}
