package com.asv.controller.main;

import com.asv.constant.DeviceStatus;
import com.asv.constant.LogRecordType;
import com.asv.dao.*;
import com.asv.entity.*;
import com.asv.utils.PermissionUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(value = "/flow")
public class FlowController {
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    IdentityService identityService;

    @Autowired
    DeviceDao deviceDao;

    @Autowired
    LogRecordDao logRecordDao;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    MailQueueDao mailQueueDao;

    @Autowired
    UserDao userDao;

    @Autowired
    BorrowHistoryDao borrowHistoryDao;

    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public String scrapApply(@RequestBody List<String> deviceIdList, HttpServletRequest httpServletRequest) {
        // 传入 scrap、delete 作为流程启动Key
        String key = httpServletRequest.getParameter("key");

        // 业务相关
        deviceIdList.forEach(deviceId -> {
            Device device = deviceDao.findByDeviceId(deviceId);
            if (device == null) {
                throw new RuntimeException("找不到该设备ID - " + deviceId);
            }
            if (device.getStatus() != DeviceStatus.IN_STORE.getValue()) {
                throw new RuntimeException("设备状态不为在库 - " + deviceId);
            }
        });

        User u = (User) httpServletRequest.getAttribute("login");

        String stater = String.valueOf(u.getUserId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String businessKey = dateFormat.format(new Date());

        // activity 相关
        identityService.setAuthenticatedUserId(stater);

        HashMap<String, Object> map = new HashMap<>();
        map.put("assignee", stater);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key + "Key", businessKey, map);

        Task task = taskService.createTaskQuery().processDefinitionKey(key + "Key").taskAssignee(stater)
                .singleResult();

        taskService.complete(task.getId());
        runtimeService.setVariable(processInstance.getId(), "processStatus", "进行中");

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(String.join(", ",
                "流程实例id：" + processInstance.getId(),
                "流程实例Name：" + processInstance.getProcessDefinitionName(),
                "流程业务Key：" + processInstance.getBusinessKey(),
                "流程开始时间：" + dateFormat1.format(processInstance.getStartTime()),
                "任务id：" + task.getId(),
                "任务名称：" + task.getName(),
                "任务处理人：" + task.getAssignee(),
                "任务创建时间：" + dateFormat1.format(task.getCreateTime())));

        deviceIdList.forEach(deviceId -> {
            Device device = deviceDao.findByDeviceId(deviceId);

            LogRecord logRecord = new LogRecord();
            logRecord.setBusinessKey(businessKey);
            logRecord.setDeviceId(device.getDeviceId());
            logRecord.setDeviceName(device.getName());
            logRecord.setDevicePath(device.getPnDevice());
            if (key.equalsIgnoreCase("scrap")) {
                logRecord.setLogType(LogRecordType.LOG_TYPE_SCRAP_APPLY);
            } else if (key.equalsIgnoreCase("delete")) {
                logRecord.setLogType(LogRecordType.LOG_TYPE_DELETE_APPLY);
            }

            logRecord.setOperatorId(u.getUserId());
            logRecord.setOperatorName(u.getUserName());
            logRecord.setOperatorDate(new Date());
            logRecord.setRemark("");

            // 保存日志记录
            logRecordDao.save(logRecord);

            // 更新设备状态
            if (key.equalsIgnoreCase("scrap")) {
                device.setStatus(DeviceStatus.SCRAPING.getValue());
            } else if (key.equalsIgnoreCase("delete")) {
                device.setStatus(DeviceStatus.DELETING.getValue());
            }

            device.setUpdateDate(new Date());
            deviceDao.saveAndFlush(device);
        });

        // 邮件
        Context context = new Context();
        context.setVariable("processId", processInstance.getId());
        context.setVariable("user", u.getUserName());
        context.setVariable("date", dateFormat1.format(new Date()));

        MailQueue mailQueue = new MailQueue();

        mailQueue.setFlag(0);
        // 获取具有审批权限的人 roleId = 3
        mailQueue.setMailTo(String.join(",", PermissionUtil.getManagerAddress()));
        mailQueue.setMailFrom(u.getMail());
        if (key.equalsIgnoreCase("scrap")) {
            mailQueue.setSubject("报废流程通知");
            mailQueue.setEmailInfo(templateEngine.process("scrapApply", context));
        } else if (key.equalsIgnoreCase("delete")) {
            mailQueue.setSubject("删除流程通知");
            mailQueue.setEmailInfo(templateEngine.process("deleteApply", context));
        }

        mailQueueDao.save(mailQueue);

        return "流程启动成功";
    }

    // 需要用下拉框的形式获取用户列表, 传入用户Id
    @RequestMapping(value = "/borrow", method = RequestMethod.POST)
    public String borrow(@RequestBody List<String> deviceIdList, HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getParameter("userId");
        String usingLine = httpServletRequest.getParameter("usingLine");
        String usingPost = httpServletRequest.getParameter("usingPost");
        String remark = httpServletRequest.getParameter("remark");

        User user = userDao.findByUserId(Integer.valueOf(userId));

        if (user == null) {
            throw new RuntimeException("无该用户 - " + userId);
        }

        // 业务相关
        deviceIdList.forEach(deviceId -> {
            Device device = deviceDao.findByDeviceId(deviceId);
            if (device == null) {
                throw new RuntimeException("找不到该设备ID - " + deviceId);
            }
        });

        deviceIdList.forEach(deviceId -> {
            Device device = deviceDao.findByDeviceId(deviceId);

            device.setCurrentUserId(userId);
            device.setCurrentUserName(user.getUserName());
            device.setStatus(DeviceStatus.USING.getValue());
            device.setBorrowDate(new Date());
            device.setUsingLine(usingLine);
            device.setUsingPost(usingPost);
//            device.setVirus(AntivirusStatus.NOT_SCANNED.getValue());
//            device.setVirusDate(null);
            device.setUpdateDate(new Date());

            deviceDao.saveAndFlush(device);

            // 借出表
            BorrowHistory borrowHistory = new BorrowHistory();
            borrowHistory.setUserId(Integer.valueOf(userId));
            borrowHistory.setUserName(user.getUserName());
            borrowHistory.setStartTime(device.getBorrowDate());
            borrowHistory.setUsingLine(usingLine);
            borrowHistory.setUserPost(usingPost);
            borrowHistory.setRemark(remark);
            borrowHistory.setDeviceId(deviceId);

            borrowHistoryDao.save(borrowHistory);

        });

        return "借出成功";
    }
}
