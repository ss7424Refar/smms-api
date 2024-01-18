package com.asv.controller.admin;

import com.asv.dao.LineDao;
import com.asv.dao.WorkDao;
import com.asv.entity.Line;
import com.asv.entity.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workLine")
public class WorkLineController {

    @Autowired
    WorkDao workDao;

    @Autowired
    LineDao lineDao;

    @RequestMapping(value = "/work/add", method = RequestMethod.POST)
    public String addWork(@RequestBody Work work) {
        Work work1 = workDao.findByName(work.getName());

        if (work1 != null) {
            throw new RuntimeException("该班别存在");
        }

        workDao.save(work);
        return "添加班别成功";
    }

    @RequestMapping(value = "/line/add", method = RequestMethod.POST)
    public String addLine(@RequestBody Line line) {
        Line line1 = lineDao.findByName(line.getName());

        if (line1 != null) {
            throw new RuntimeException("该线别存在");
        }

        lineDao.save(line);
        return "添加线别成功";
    }

    @PostMapping(value = "/work/delete")
    public String deleteWork(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            Work work = workDao.findById(id);
            if (work == null) {
                throw new RuntimeException("没有该班别 - " + id);
            }
        }

        workDao.deleteByIds(ids);
        return "删除成功";
    }

    @PostMapping(value = "/line/delete")
    public String deleteLine(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            Line line = lineDao.findById(id);
            if (line == null) {
                throw new RuntimeException("没有该线别 - " + id);
            }
        }

        lineDao.deleteByIds(ids);
        return "删除成功";
    }

    @PostMapping(value = "/work/update")
    public String updateWork(@Valid @RequestBody Work work) {
        Work work1 = workDao.findById(work.getId());

        if (null == work1) {
            throw new RuntimeException("没有该班别");
        }

        work1.setName(work.getName());

        workDao.saveAndFlush(work1);
        return "更新班别成功";
    }

    @PostMapping(value = "/line/update")
    public String updateLine(@Valid @RequestBody Line line) {
        Line line1 = lineDao.findById(line.getId());

        if (null == line1) {
            throw new RuntimeException("没有该线别");
        }

        line1.setName(line.getName());

        lineDao.saveAndFlush(line1);
        return "更新线别成功";
    }

    @PostMapping(value = "/work/select")
    public Map<String, Object> selectWork(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Page<Work> page = workDao.findAll(PageRequest.of(pageNum, pageSize));

        Map<String, Object> map = new HashMap<>();
        map.put("rows", page.getContent());
        map.put("total", page.getTotalElements());

        return map;

    }

    @PostMapping(value = "/line/select")
    public Map<String, Object> selectLine(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Page<Line> page = lineDao.findAll(PageRequest.of(pageNum, pageSize));

        Map<String, Object> map = new HashMap<>();
        map.put("rows", page.getContent());
        map.put("total", page.getTotalElements());

        return map;

    }
}


