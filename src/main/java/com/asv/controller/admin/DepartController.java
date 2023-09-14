package com.asv.controller.admin;

import com.asv.dao.DepartDao;
import com.asv.dao.SectionDao;
import com.asv.entity.Depart;
import com.asv.entity.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/depart")
public class DepartController {

    @Autowired
    DepartDao departDao;

    @Autowired
    SectionDao sectionDao;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addDepart(HttpServletRequest request) {
        String departName = request.getParameter("departName");

        Depart depart = new Depart();
        depart.setName(departName);
        departDao.save(depart);

        return "添加部门成功";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateDepart(HttpServletRequest request) {
        Integer departId = Integer.valueOf(request.getParameter("departId"));
        String name = request.getParameter("departName");

        Depart depart = departDao.findById(departId);

        if (depart != null) {
            depart.setName(name);
            departDao.saveAndFlush(depart);

            return "更新部门成功";
        } else {
            throw new RuntimeException("查找部门失败");
        }
    }

    @RequestMapping(value = "/select", method = RequestMethod.POST)
    public Map<String, Object> selectDepart(@RequestParam("departName") String name, @RequestParam("pageNum") Integer pageNum,
                                            @RequestParam("pageSize") Integer pageSize) {

        Page<Depart> pageList;
        Pageable pageable = (Pageable) PageRequest.of(pageNum, pageSize);
        if (name != null && !name.isEmpty()) {
            pageList = departDao.selectAllByDepartName(name, pageable);
        } else {
            pageList = departDao.findAll(pageable);
        }

        List<Depart> departList = pageList.getContent();
        // 使用map()方法对每个Depart对象进行操作，将其转换为包含部门和部门下所有小节的Map对象。
        // 最后使用collect()方法将结果收集到一个新的List中。
        List<Map<String, Object>> list = departList.stream().map(depart -> {
            List<Section> sectionList = sectionDao.findAllByDepartId(depart.getId());
            Map<String, Object> map = new HashMap<>();
            map.put("depart", depart);
            map.put("section", sectionList);
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", list);
        resultMap.put("total", pageList.getTotalElements());

        return resultMap;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteDepart(HttpServletRequest request) {
        Integer departId = Integer.valueOf(request.getParameter("departId"));
        Depart depart = departDao.findById(departId);

        if (depart != null) {
            departDao.delete(depart);
            return "删除部门成功";
        } else {
            throw new RuntimeException("查找部门失败");
        }
    }

    @RequestMapping(value = "/addSection", method = RequestMethod.POST)
    public String addSection(HttpServletRequest request) {
        Integer departId = Integer.valueOf(request.getParameter("departId"));
        String sectionName = request.getParameter("sectionName");

        // 这里可以不用考虑重复
        Section section = new Section();
        section.setDepartId(departId);
        section.setName(sectionName);

        sectionDao.save(section);

        return "添加课成功";
    }

    @RequestMapping(value = "/deleteSection", method = RequestMethod.POST)
    public String deleteSection(HttpServletRequest request) {
        Integer sectionId = Integer.valueOf(request.getParameter("sectionId"));
        Section section = sectionDao.findById(sectionId);

        if (section != null) {
            sectionDao.delete(section);
            return "删除课成功";
        } else {
            throw new RuntimeException("查找课失败");
        }
    }

    @RequestMapping(value = "/updateSection", method = RequestMethod.POST)
    public String updateSection(HttpServletRequest request) {
        Integer sectionId = Integer.valueOf(request.getParameter("sectionId"));
        String sectionName = request.getParameter("sectionName");
        Section section = sectionDao.findById(sectionId);

        if (section != null) {
            section.setName(sectionName);
            sectionDao.saveAndFlush(section);
            return "更新课成功";
        } else {
            throw new RuntimeException("查找课失败");
        }
    }

}
