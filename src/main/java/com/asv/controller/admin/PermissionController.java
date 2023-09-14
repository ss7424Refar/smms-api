package com.asv.controller.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.asv.dao.PermissionDao;
import com.asv.entity.Permission;
import com.asv.model.PermissionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    PermissionDao permissionDao;

    @PostMapping(value = "/getRolePermission")
    public Map<String, Object> getRolePermission(HttpServletRequest httpServletRequest) {
        Integer roleId = Integer.valueOf(httpServletRequest.getParameter("roleId"));

        List<Permission> permissionList = permissionDao.findByRoleId(roleId);
        List<PermissionModel> allCode = permissionDao.findDistinctCode();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("roleId", roleId);

        List<Map<String, Object>> list = new ArrayList<>();

        for (PermissionModel s : allCode) {
            Map<String, Object> temp1 = new HashMap<>();
            for (Permission permission : permissionList) {
                if (permission.getCode().equalsIgnoreCase(s.getPermissionCode())) {
                    temp1.put("code", s.getPermissionCode());
                    temp1.put("desc", s.getPermissionDesc());
                    temp1.put("check", Boolean.TRUE);
                    break;
                }
            }

            if (!temp1.containsKey("code")) {
                temp1.put("code", s.getPermissionCode());
                temp1.put("desc", s.getPermissionDesc());
                temp1.put("check", Boolean.FALSE);
            }

            list.add(temp1);
        }

        resultMap.put("detail", list);
        return resultMap;

    }

    @PostMapping(value = "/update")
    public String update(@RequestBody JSONObject json) {
        Integer roleId = Integer.valueOf(json.getString("roleId"));
        // 获取detail字段的值，即JSONArray
        JSONArray detailArray = json.getJSONArray("detail");

        // 遍历JSONArray中的每个元素
        for (int i = 0; i < detailArray.size(); i++) {
            JSONObject item = detailArray.getJSONObject(i);
            String code = item.getString("code");
            boolean check = item.getBoolean("check");
            String desc = item.getString("desc");

            Permission permission = permissionDao.findByRoleIdAndCode(roleId, code);

            if (permission != null) {
                if (!check) {
                    log.info("{} / {} 删除权限", code, desc);
                    // 删除该行数据
                    permissionDao.delete(permission);
                }else {
                    log.info("{} / {} 无需更新", code, desc);
                }
            } else {
                if (check) {
                    log.info("{} / {} 增加(无权限)", code, desc);

                    Permission permission1 = new Permission();
                    permission1.setCode(code);
                    permission1.setRoleId(roleId);
                    permission1.setDesc(desc);
                    permissionDao.saveAndFlush(permission1);
                } else {
                    log.info("{} / {} 无需增加(无权限)", code, desc);
                }
            }
        }

        return "更新权限成功";
    }
}
