package com.business.notification.api;

import com.business.notification.dataclasses.NotificationRequest;
import com.business.notification.dataclasses.NotificationResponse;
import com.business.notification.service.EmailNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.net.MalformedURLException;

@Slf4j
@RestController
@RequestMapping("/notification")
public class NotificationApi {
	
	@Autowired private EmailNotificationService service;

    @PostMapping(path = "/send/email", consumes =MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> sendEmail(@RequestBody NotificationRequest notificationRequest) throws MessagingException, MalformedURLException {
        log.info("notification request: {}", notificationRequest);
        NotificationResponse response=service.send(notificationRequest);
        return new ResponseEntity<>((NotificationResponse)response,HttpStatus.OK);
    }

}
