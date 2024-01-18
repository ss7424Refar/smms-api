package com.asv.controller.main;

import com.asv.constant.AntivirusStatus;
import com.asv.constant.DeviceStatus;
import com.asv.dao.*;
import com.asv.entity.*;
import com.asv.http.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/selection")
public class OptionController {
    @Autowired
    UserDao userDao;
    @Autowired
    DepartDao departDao;
    @Autowired
    SectionDao sectionDao;
    @Autowired
    PostDao postDao;
    @Autowired
    RoleDao roleDao;
    @Autowired
    LineDao lineDao;
    @Autowired
    WorkDao workDao;

    @RequestMapping(value = "/getUsers", method = RequestMethod.POST)
    public List<Map<String, String>> getUserDropdownData() {
        List<User> users = userDao.findAll();

        List<Map<String, String>> result = new ArrayList<>();
        users.forEach(user -> {
            Map<String, String> map = new HashMap<>();
            map.put("text", user.getUserId().toString());
            map.put("value", user.getUserName());

            result.add(map);

        });

        return result;
    }

    @RequestMapping(value = "/getScanUsers", method = RequestMethod.POST)
    public ResponseResult getScanUserDropdownData(HttpServletRequest httpServletRequest) {
        String userNo = httpServletRequest.getParameter("userNo");
        User user = userDao.findByUserNo(userNo);

        if (user == null) {
            throw new RuntimeException("无此员工号 - " + userNo);
        }

        return ResponseResult.success(user);
    }

    @RequestMapping(value = "/getDeparts", method = RequestMethod.POST)
    public List<Map<String, String>> getDepartDropdownData() {
        List<Depart> departs = departDao.findAll();

        List<Map<String, String>> result = new ArrayList<>();
        departs.forEach(depart -> {
            Map<String, String> map = new HashMap<>();
            map.put("text", depart.getId().toString());
            map.put("value", depart.getName());

            result.add(map);

        });

        return result;
    }

    @RequestMapping(value = "/getSections", method = RequestMethod.POST)
    public List<Map<String, String>> getSectionDropdownData(HttpServletRequest httpServletRequest) {

        String departId = httpServletRequest.getParameter("departId");
        List<Section> sections = sectionDao.findAllByDepartId(Integer.valueOf(departId));

        List<Map<String, String>> result = new ArrayList<>();
        sections.forEach(section -> {
            Map<String, String> map = new HashMap<>();
            map.put("text", section.getId().toString());
            map.put("value", section.getName());

            result.add(map);

        });

        return result;
    }

    @RequestMapping(value = "/getPosts", method = RequestMethod.POST)
    public List<Map<String, String>> getPostDropdownData() {

        List<Post> posts = postDao.findAll();

        List<Map<String, String>> result = new ArrayList<>();
        posts.forEach(post -> {
            Map<String, String> map = new HashMap<>();
            map.put("text", post.getId().toString());
            map.put("value", post.getName());

            result.add(map);

        });

        return result;
    }

    @RequestMapping(value = "/getRoles", method = RequestMethod.POST)
    public List<Map<String, String>> getRoleDropdownData() {

        List<Role> roles = roleDao.findAll();

        List<Map<String, String>> result = new ArrayList<>();
        roles.forEach(role -> {
            Map<String, String> map = new HashMap<>();
            map.put("text", role.getId().toString());
            map.put("value", role.getDesc());

            result.add(map);

        });

        return result;
    }

    @RequestMapping(value = "/getDeviceStatus", method = RequestMethod.POST)
    public List<Map<String, String>> getDeviceStatusDropdownData() {

        return Arrays.stream(DeviceStatus.values())
                .map(status -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("text", status.getDesc());
                    map.put("value", String.valueOf(status.getValue()));
                    return map;
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/getVirusStatus", method = RequestMethod.POST)
    public List<Map<String, String>> getVirusStatusDropdownData() {

        return Arrays.stream(AntivirusStatus.values())
                .map(status -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("text", status.getDescription());
                    map.put("value", String.valueOf(status.getValue()));
                    return map;
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/getLines", method = RequestMethod.POST)
    public List<Map<String, String>> getLineDropdownData() {

        List<Line> lines = lineDao.findAll();

        List<Map<String, String>> result = new ArrayList<>();
        lines.forEach(line -> {
            Map<String, String> map = new HashMap<>();
            map.put("text", line.getId().toString());
            map.put("value", line.getName());

            result.add(map);

        });

        return result;
    }

    @RequestMapping(value = "/getWorks", method = RequestMethod.POST)
    public List<Map<String, String>> getWorkDropdownData() {

        List<Work> roles = workDao.findAll();

        List<Map<String, String>> result = new ArrayList<>();
        roles.forEach(work -> {
            Map<String, String> map = new HashMap<>();
            map.put("text", work.getId().toString());
            map.put("value", work.getName());

            result.add(map);

        });

        return result;
    }
}
