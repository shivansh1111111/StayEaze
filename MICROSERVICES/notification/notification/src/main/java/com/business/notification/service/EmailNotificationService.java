package com.business.notification.service;


import com.business.notification.dataclasses.NotificationRequest;
import com.business.notification.dataclasses.NotificationResponse;
import com.business.notification.dataclasses.Responses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.net.MalformedURLException;

@Service
@Slf4j
public class EmailNotificationService{
	private static final String ROOT_ENV_KEY="com.stayeaze.email";
	@Autowired private Environment env;
	@Autowired private JavaMailSender mailSender;
	@Autowired private EmailContentBuilder emailContentBuilder;

	public NotificationResponse send(NotificationRequest req) throws  MessagingException, MalformedURLException {
		log.info("emailCode >> {}",req.getEmailCode());
		String isEnable=env.getProperty(String.format("%s.%s.enable", ROOT_ENV_KEY, req.getEmailCode()),"false");
		log.info("{}.emailCode.enable >> {} : isEnable: {}",ROOT_ENV_KEY,req.getEmailCode(),isEnable);
		if(Boolean.parseBoolean(isEnable)){
			MimeMessage message=mailSender.createMimeMessage();
			emailContentBuilder.createEmail(req, message);
			mailSender.send(message);
			log.info("email sent successfully");
		}
		return new NotificationResponse(Responses.SUCCESS);
	}

}
