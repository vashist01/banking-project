package com.banking.notification.pattern;

import com.banking.notification.enums.NotificationChanel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationFactory {

    private final Map<NotificationChanel, NotificationStrategy> notificationStrategy;

    public NotificationFactory(List<NotificationStrategy> strategies) {
        notificationStrategy = strategies.stream().
                collect(Collectors.toMap(NotificationStrategy::getChanel, Function.identity()));
    }

    public NotificationStrategy getStrategy(NotificationChanel notificationChanel){
        return notificationStrategy.get(notificationChanel);
    }
    public Collection<NotificationStrategy> getAllStrategies() {

        return notificationStrategy.values();
    }
}
