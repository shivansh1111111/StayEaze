package com.business.booking.clients.notification;

import com.business.booking.util.dataclasses.NotificationRequest;
import com.business.booking.util.dataclasses.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${notification.service.base-url:http://stayeaze:9005}")
    private String baseUrl;

    @Value("${notification.service.endpoint:/notification/send/email}")
    private String endpoint;

    public NotificationResponse sendNotification(NotificationRequest notificationRequest) {
        try {
            // Build full URL
            String url = baseUrl + endpoint;

            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Wrap request with headers
            HttpEntity<NotificationRequest> entity = new HttpEntity<>(notificationRequest, headers);

            // Make POST call
            ResponseEntity<NotificationResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    NotificationResponse.class
            );

            log.info("Notification sent successfully to: {} | Status: {}",
                    notificationRequest.getTo(),
                    response.getStatusCode());

            return response.getBody();

        } catch (Exception ex) {
            log.error("Error sending notification to {}: {}",
                    notificationRequest.getTo(),
                    ex.getMessage(), ex);
            return new NotificationResponse("FAILURE", ex.getMessage());
        }
    }
}
