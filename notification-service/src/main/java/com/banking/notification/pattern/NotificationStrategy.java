package com.banking.notification.pattern;

import com.banking.notification.enums.NotificationChanel;
import com.banking.notification.dto.request.NotificationRequest;

public interface NotificationStrategy {

    NotificationChanel getChanel();

    void send(NotificationRequest notificationRequest);
}
