package com.asv.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 存放常量
 */
@Component
public class Constant {

    public static String IMAGE_PATH;//设置上传图片的存储路径
    @Value("${my-config.image-path}") //需要spring注入这个value，因此需要创建bean，使用@component注解
    public void setImagePath(String imagePath) {
        IMAGE_PATH = imagePath;
    }

    public static String VIRUS_LOG_PATH;
    @Value("${my-config.virus-log-path}")
    public void setVirusLogPath(String virusLogPath) {
        VIRUS_LOG_PATH = virusLogPath;
    }

    public static String ACTIVE;
    @Value("${spring.profiles.active}")
    public void setActive(String active) {
        ACTIVE = active;
    }

    public static String MAIL_FROM;
    @Value("${my-config.mail-from}")
    public void setMailFrom(String mailFrom) {
        MAIL_FROM = mailFrom;
    }

    public static Boolean SEND_MAIL;
    @Value("${my-config.send-mail}")
    public void setSendMail(Boolean sendMail) {
        SEND_MAIL = sendMail;
    }

    public static String RELEASE_PATH;
    @Value("${my-config.release-path}")
    public void setReleasePath(String releasePath) {
        RELEASE_PATH = releasePath;
    }
}
