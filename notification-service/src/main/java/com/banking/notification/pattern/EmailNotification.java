package com.banking.notification.pattern;

import com.banking.notification.enums.NotificationChanel;
import com.banking.notification.dto.request.NotificationRequest;

public class EmailNotification implements NotificationStrategy {
    @Override
    public NotificationChanel getChanel() {
        return NotificationChanel.EMAIL;
    }

    @Override
    public void send(NotificationRequest notificationRequest) {

    }
}
