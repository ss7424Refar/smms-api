package com.asv;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestPostfix {
    public static void main(String[] args) {
        // 配置SMTP服务器信息
        String host = "172.30.184.33";
        int port = 25;

        // 发件人和收件人信息
        String from = "asvTech@dbh.dynabook.com";
        String to = "linzhu@dbh.dynabook.com";

        // 邮件主题和正文
        String subject = "Hello, World!";
        String body = "This is the content of the email.";

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        MimeMessage message = sender.createMimeMessage();
        sender.setHost(host);
        sender.setPort(port);

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);
            sender.send(message);
        } catch (MessagingException e) {
            System.out.println("Send Email Has Error,Cause By:" + e.getMessage() + e.fillInStackTrace());
        }
    }
}
