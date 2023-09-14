package com.asv.controller.admin;

import com.asv.http.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
public class IndexController {
    @Autowired
    JavaMailSender sender;
    @Autowired
    TemplateEngine templateEngine;

    @RequestMapping("/index")
    public String index() {
        return "hello";
    }

    @GetMapping("/wrong")
    public ResponseResult error() {
//        int i = 9/0;
//        return ResponseResult.fail("11111");
        throw new RuntimeException("自定义异常");
    }

//    @RequestMapping("/jpa")
//    public void testJPA1() {
//        userRepository.save(User.builder().name("A").age(11).build());
//        userRepository.save(User.builder().name("B").age(12).build());
//        userRepository.save(User.builder().name("C").age(13).build());
//
//    }

//    @GetMapping("/jpa1")
//    public User testJPA2() {
//        // 需要生成getter方法
//        return userRepository.findUser("A");
//    }

//    @GetMapping("/jpa2")
//    public User testJPA3() {
//        // 需要生成getter方法
//        return userRepository.findUser2("A");
//    }

    @RequestMapping(value = "/send")
    public void sendMail() {
        {
            // 配置SMTP服务器信息
//            String host = "172.30.184.33";
//            int port = 25;

            // 发件人和收件人信息
            String from = "asvTech@dbh.dynabook.com";
            String to = "linzhu@dbh.dynabook.com";

            // 邮件主题和正文
            String subject = "Hello, World!";
            String body = "This is the content of the email.";

            MimeMessage message = sender.createMimeMessage();

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

}

