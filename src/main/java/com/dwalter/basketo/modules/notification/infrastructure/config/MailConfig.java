package com.dwalter.basketo.modules.notification.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

@Configuration
public class MailConfig {

    /**
     * Only create JavaMailSender bean when app.mail.mode=smtp
     * This prevents SMTP connection errors when running in console or fake mode
     */
    @Bean
    @ConditionalOnProperty(name = "app.mail.mode", havingValue = "smtp")
    public JavaMailSender javaMailSender(
            @Value("${spring.mail.host}") String host,
            @Value("${spring.mail.port}") int port,
            @Value("${spring.mail.username}") String username,
            @Value("${spring.mail.password}") String password,
            @Value("${spring.mail.properties.mail.smtp.auth}") boolean auth,
            @Value("${spring.mail.properties.mail.smtp.starttls.enable}") boolean starttlsEnable,
            @Value("${spring.mail.properties.mail.smtp.starttls.required}") boolean starttlsRequired,
            @Value("${spring.mail.properties.mail.smtp.connectiontimeout}") int connectionTimeout,
            @Value("${spring.mail.properties.mail.smtp.timeout}") int timeout,
            @Value("${spring.mail.properties.mail.smtp.writetimeout}") int writeTimeout
    ) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.smtp.starttls.required", starttlsRequired);
        props.put("mail.smtp.connectiontimeout", connectionTimeout);
        props.put("mail.smtp.timeout", timeout);
        props.put("mail.smtp.writetimeout", writeTimeout);

        return mailSender;
    }
}
