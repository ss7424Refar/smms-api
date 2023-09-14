package com.asv.controller.common;

import com.asv.constant.Constant;
import com.asv.dao.MailQueueDao;
import com.asv.dao.UserDao;
import com.asv.entity.MailQueue;
import com.asv.entity.User;
import com.asv.utils.PasswordUtils;
import com.asv.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping(value = "/login")
public class LoginController {
    @Autowired
    private UserDao userDao;
    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    JavaMailSender sender;
    @Autowired
    MailQueueDao mailQueueDao;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, String mail, String password) {
        User user = userDao.findByMail(mail);
        if (null == user) {
            log.error("登录失败");
            throw new RuntimeException("邮箱帐号不正确");
        } else {
            if (PasswordUtils.encode(password).equals(user.getPassword())) {
                log.info(user.getUserNo() + " - " + user.getUserName() + " 登录成功");

                // 返回token
                return TokenUtil.generateToken(String.valueOf(user.getUserId()), user.getUserName());

            } else {
                throw new RuntimeException("密码不正确");
            }
        }

    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public String forgot(HttpServletRequest request) {
        String userNo1 = request.getParameter("userNo");
        User user = userDao.findByUserNo(userNo1);
        if (user == null) {
            throw new RuntimeException("没有该用户");
        }

        // 随机生成密码
        String newPassword = PasswordUtils.hashPassword(PasswordUtils.generateRandomString());

        Context context = new Context();
        context.setVariable("newPassword", newPassword);

        MailQueue mailQueue = new MailQueue();

        mailQueue.setFlag(0);
        mailQueue.setMailTo(user.getMail());
        mailQueue.setMailFrom(Constant.MAIL_FROM);
        mailQueue.setSubject("密码重置通知 - 忘记密码");
        mailQueue.setEmailInfo(templateEngine.process("forgotPassword", context));

        mailQueueDao.save(mailQueue);

        return "重置密码成功, 请接收邮件查看 ~";
    }
}
