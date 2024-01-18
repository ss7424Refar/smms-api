package com.asv.controller.admin;

import com.asv.constant.Constant;
import com.asv.dao.UserDao;
import com.asv.entity.User;
import com.asv.http.ResponseResult;
import com.asv.model.UserModel;
import com.asv.utils.PasswordUtils;
import com.asv.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserDao userDao;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    // 这里需要前端传入user bean
    public String addUser(@Valid @RequestBody User user) {
        User user1 = userDao.findByUserId(user.getUserId());

        if (null != user1) {
            throw new RuntimeException("存在重复员工号");
        }

        user.setIcons("default.png");
        user.setPassword(PasswordUtils.encode(user.getPassword()));
        userDao.save(user);

        return "添加用户成功";
    }

    // 使用post的话, 需要在body中直接传入数组, 不需要任何参数名字
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public String deleteUser(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            User user = userDao.findByUserId(id);
            if (user == null) {
                throw new RuntimeException("没有该用户 - " + id);
            }
        }

        userDao.deleteByIds(ids);
        return "删除成功";
    }

    @PostMapping(value = "/getUpdateInfo")
    public ResponseResult getUpdateInfo(HttpServletRequest httpServletRequest) {
        UserModel userModel = userDao.getUserUpdateInfo(Integer.valueOf(httpServletRequest.getParameter("userId")));
        if (null == userModel) {
            throw new RuntimeException("没有该用户");
        }

        return ResponseResult.success(userModel);
    }

    @PostMapping(value = "/update")
    public String updateUser(@Valid @RequestBody User user) {

        User user1 = userDao.findByUserId(user.getUserId());

        if (null == user1) {
            throw new RuntimeException("没有该用户");
        }

        user.setIcons(user1.getIcons());
        user.setPassword(user1.getPassword());
        userDao.saveAndFlush(user);
        return "更新用户成功";
    }

    // 页码从0开始
    @PostMapping(value = "/select")
    public Map<String, Object> selectUser(HttpServletRequest request,
                                          @RequestParam(value = "userNo", required = false) String userNo,
                                          @RequestParam(value = "userName", required = false) String userName,
                                          @RequestParam(value = "status", required = false) String status,
                                          @RequestParam(value = "departId", required = false) Integer departId,
                                          @RequestParam(value = "sectionId", required = false) Integer sectionId,
                                          @RequestParam(value = "mail", required = false) String mail,
                                          @RequestParam("pageNum") int pageNum,
                                          @RequestParam("pageSize") int pageSize) {

        Pageable pageable = PageRequest.of(pageNum, pageSize);
        if (ObjectUtils.isEmpty(userNo)) {
            userNo = null;
        }
        if (ObjectUtils.isEmpty(userName)) {
            userName = null;
        }
        if (ObjectUtils.isEmpty(status)) {
            status = null;
        }
        if (ObjectUtils.isEmpty(mail)) {
            mail = null;
        }

        Page<UserModel> page = userDao.searchUsersByKeyword(userNo, userName, status, departId, sectionId, mail, pageable);
//        Page<UserModel> page = userDao.searchUsersByKeyword(userNo, userName, pageable);

        // peek 一种中间操作
        List<UserModel> newUserList = page.getContent().stream().peek(
                user -> user.setIcons(getHost(request) + "/image/" + user.getIcons())
        ).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("rows", newUserList);
        map.put("total", page.getTotalElements());

        return map;

    }

    @PostMapping(value = "/uploadImage")
    public String upLoad(HttpServletRequest httpServletRequest, @RequestParam("file1") MultipartFile file) {
        Integer userId = Integer.valueOf(httpServletRequest.getParameter("userId"));

        User user = userDao.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("没有当前用户");
        }

        //获取上传文件的名字
        String fileName = file.getOriginalFilename();
        //获取上传文件的后缀名
        assert fileName != null;
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //生成文件名称UUID
        UUID uuid = UUID.randomUUID();
        //生成新的文件名称
        String fileNewName = uuid + suffixName;
        //上传图片的存放目录
        File fileDirectory = new File(Constant.IMAGE_PATH);
        //上传图片的存放路径
        File destFile = new File(Constant.IMAGE_PATH + fileNewName);
        //判断目录是否存在
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {//目录创建失败
                throw new RuntimeException("创建目录失败");
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setIcons(fileNewName);
        userDao.saveAndFlush(user);
        return getHost(httpServletRequest) + "/image/" + fileNewName;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(HttpServletRequest request) {
        String userNo1 = request.getParameter("userNo");
        String password = request.getParameter("password");
        String token = request.getHeader("Authorization");

        User user = userDao.findByUserNo(userNo1);
        if (user == null) {
            throw new RuntimeException("没有该用户");
        }

        user.setPassword(PasswordUtils.encode(password));
        userDao.saveAndFlush(user);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            TokenUtil.invalidateToken(token);
        }

        return "修改密码成功";
    }

    //获取URI：http://localhost:8088/uapi
    private String getHost(HttpServletRequest request) {

        URI effectiveURI;
        try {
            URI uri = new URI(String.valueOf(request.getRequestURL()));
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                    null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        assert effectiveURI != null;
        return effectiveURI.toString() + request.getContextPath();

    }
}