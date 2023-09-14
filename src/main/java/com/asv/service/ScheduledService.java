package com.asv.service;

import com.asv.constant.Constant;
import com.asv.dao.MailQueueDao;
import com.asv.entity.MailQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class ScheduledService {
    @Autowired
    MailQueueDao mailQueueDao;
    @Autowired
    JavaMailSender sender;
    @Autowired
    TemplateEngine templateEngine;

    @Scheduled(cron = "0/5 * * * * *")
    public void sendMail() throws MessagingException {
        if (Constant.SEND_MAIL) {
            List<MailQueue> list = mailQueueDao.findByFlag(0);

            for (MailQueue mail : list) {
                MimeMessage message = sender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

                try {
                    mimeMessageHelper.setTo(mail.getMailTo().split(","));
                    mimeMessageHelper.setFrom(mail.getMailFrom());
                    mimeMessageHelper.setText(mail.getEmailInfo());
                    mimeMessageHelper.setSubject(mail.getSubject());
                    mimeMessageHelper.setText(mail.getEmailInfo(), true);
                    sender.send(message);

                    mailQueueDao.deleteById(mail.getId());
                    log.info("id:{} - [from] {}, [to]{} -->> {} 发送邮件成功 ", mail.getId(), mail.getSubject(), mail.getMailFrom(), mail.getMailTo());
                } catch (MessagingException e) {
                    log.error("Send Email Has Error,Cause By:" + e.getMessage(), e.fillInStackTrace());
                }
            }

        }
    }

}
