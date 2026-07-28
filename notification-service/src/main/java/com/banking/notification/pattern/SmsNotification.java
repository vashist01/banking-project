package com.banking.notification.pattern;

import com.banking.notification.enums.NotificationChanel;
import com.banking.notification.dto.request.NotificationRequest;

public class SmsNotification  implements NotificationStrategy {
    @Override
    public NotificationChanel getChanel() {
        return NotificationChanel.SMS;
    }

    @Override
    public void send(NotificationRequest notificationRequest) {

    }
}
