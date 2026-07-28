package com.banking.notification.pattern;

import com.banking.notification.enums.NotificationChanel;
import com.banking.notification.dto.request.NotificationRequest;

public class PushNotification implements NotificationStrategy{
    @Override
    public NotificationChanel getChanel() {
        return NotificationChanel.PUSH;
    }

    @Override
    public void send(NotificationRequest notificationRequest) {

    }
}
