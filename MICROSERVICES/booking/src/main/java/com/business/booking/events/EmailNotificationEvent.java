package com.business.booking.events;

import com.business.booking.util.dataclasses.NotificationRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmailNotificationEvent extends ApplicationEvent {
    private final NotificationRequest notificationRequest;

    public EmailNotificationEvent(Object source, NotificationRequest notificationRequest) {
        super(source);
        this.notificationRequest = notificationRequest;
    }
}

