package com.business.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Slf4j
@Configuration
public class EmailConfig{

    @Autowired
    private Environment env;

    private int port;
    private String host;
    private String userName;
    private String password;
    private String protocol;
    private String auth;
    private String starttlsEnable;
    private String mailDebugEnable;
    private String smtpTrust;


    @PostConstruct
    void init(){
    	log.info("Email Configuration Started...");
        this.port=Integer.parseInt(env.getProperty("spring.mail.port"));
        this.host=env.getProperty("spring.mail.host");
        this.userName=env.getProperty("spring.mail.username");
        this.password=env.getProperty("spring.mail.password");
        this.protocol=env.getProperty("spring.mail.protocol");
        this.auth=env.getProperty("spring.mail.properties.mail.smtp.auth");
        this.starttlsEnable=env.getProperty("spring.mail.properties.mail.smtp.starttls.enable");
        this.mailDebugEnable=env.getProperty("spring.mail.properties.mail.debug");
        this.smtpTrust=env.getProperty("spring.mail.properties.mail.smtp.trust");
        log.info("Email Configuration Done...");
        log.debug(this.toString());
    }


    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.host);
        mailSender.setPort(this.port);
        if(null !=this.userName) mailSender.setUsername(this.userName);
        if(null !=this.password) mailSender.setPassword(this.password);

        Properties props = mailSender.getJavaMailProperties();
        if(null !=this.protocol) props.put("mail.transport.protocol", this.protocol);
        if(null !=this.auth) props.put("mail.smtp.auth", this.auth);
        if(null !=this.starttlsEnable) props.put("mail.smtp.starttls.enable", this.starttlsEnable);
        if(null !=this.mailDebugEnable) props.put("mail.debug", this.mailDebugEnable);
        if(null !=this.smtpTrust) props.put("mail.smtp.ssl.trust", this.smtpTrust);
        return mailSender;
    }

    @Bean
    public SimpleMailMessage emailTemplate()
    {
        SimpleMailMessage message = new SimpleMailMessage();
        return message;
    }


	@Override
	public String toString() {
		return "EmailConfig [port=" + port + ", host=" + host + ", userName=" + userName + ", password="
				+ "*******, protocol=" + protocol + ", auth=" + auth + ", starttlsEnable=" + starttlsEnable
				+ ", mailDebugEnable=" + mailDebugEnable + ", smtpTrust=" + smtpTrust + "]";
	}
}
