package com.asv;

import lombok.extern.slf4j.Slf4j;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Slf4j
@SpringBootTest
@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
public class TestMail {
    @Autowired
    JavaMailSender sender;
    @Autowired
    TemplateEngine templateEngine;

//    @Test
    public void thymeleafMailMessage() {
        MimeMessage message = sender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setTo("812647742@qq.com");
            mimeMessageHelper.setFrom("m15268837061@163.com");
            mimeMessageHelper.setSubject("Office Automation Password Reset");

            Context context = new Context();
            context.setVariable("username", "qx");
            context.setVariable("position", "总经理");
            context.setVariable("salary", "10000");
            // 返回渲染后的内容 通过邮件等发送出去
            String html = templateEngine.process("email", context);

            mimeMessageHelper.setText(html, true);
            sender.send(message);
        } catch (MessagingException e) {
            log.error("Send Email Has Error,Cause By:" + e.getMessage(), e.fillInStackTrace());
        }
    }


}
