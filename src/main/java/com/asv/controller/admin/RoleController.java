package com.asv.controller.admin;

import com.asv.dao.RoleDao;
import com.asv.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleDao roleDao;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addRole(@RequestBody Role role) {
        Role role1 = roleDao.findByName(role.getName());

        if (role1 != null) {
            throw new RuntimeException("该角色code存在");
        }

        roleDao.save(role);
        return "添加角色成功";
    }

    @PostMapping(value = "/delete")
    public String deleteRole(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            Role role = roleDao.findById(id);
            if (role == null) {
                throw new RuntimeException("没有该角色 - " + id);
            }
        }

        roleDao.deleteByIds(ids);
        return "删除成功";
    }

    @PostMapping(value = "/update")
    public String updateRole(@Valid @RequestBody Role role) {
        Role role1 = roleDao.findByName(role.getName());

        if (null == role1) {
            throw new RuntimeException("没有该角色");
        }

        role1.setDesc(role.getDesc());
        role1.setName(role.getName());

        roleDao.saveAndFlush(role1);
        return "更新角色成功";
    }

    @PostMapping(value = "/select")
    public Map<String, Object> selectRole(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Page<Role> page = roleDao.findAll(PageRequest.of(pageNum, pageSize));

        Map<String, Object> map = new HashMap<>();
        map.put("rows", page.getContent());
        map.put("total", page.getTotalElements());

        return map;

    }

}


