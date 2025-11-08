package com.business.auth.events;

import com.business.auth.client.NotificationClient;
import com.business.auth.dataclasses.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final NotificationClient notificationClient;

    @Async  //  run in separate thread
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailNotification(EmailNotificationEvent event) {
        try {
            log.info("Triggering email notification for: {}", event.getNotificationRequest().getTo());
            NotificationResponse response = notificationClient.sendNotification(event.getNotificationRequest());
            log.info("Notification response: {}", response);
        } catch (Exception ex) {
            log.error("Failed to send notification: {}", ex.getMessage(), ex);
        }
    }
}

