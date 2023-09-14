package com.asv.utils;

import com.asv.dao.UserDao;
import com.asv.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionUtil {
    private static final Integer MANAGE_ROLE_ID = 3;
    @Autowired
    UserDao userDao;
    static UserDao staticUserDao;

    @PostConstruct
    public void init() {
        staticUserDao = userDao;
    }

    public static String[] getManagerAddress() {
        List<User> userList = staticUserDao.findByRoleId(MANAGE_ROLE_ID);
        return userList.stream().map(User::getMail).toArray(String[]::new);
    }

    public static List<String> getManagerUserIds() {
        List<User> userList = staticUserDao.findByRoleId(MANAGE_ROLE_ID);
        return userList.stream().map(user -> {
            return String.valueOf(user.getUserId());
        }).collect(Collectors.toList());

    }
}
