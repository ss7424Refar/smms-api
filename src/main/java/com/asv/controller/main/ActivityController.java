package com.asv.controller.main;

import com.asv.constant.DeviceStatus;
import com.asv.constant.LogRecordType;
import com.asv.dao.DeviceDao;
import com.asv.dao.LogRecordDao;
import com.asv.dao.MailQueueDao;
import com.asv.dao.UserDao;
import com.asv.entity.Device;
import com.asv.entity.LogRecord;
import com.asv.entity.MailQueue;
import com.asv.entity.User;
import com.asv.model.LogRecordModel;
import com.asv.model.ProcessModel;
import com.asv.model.TaskModel;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = "/activity")
public class ActivityController {
    @Autowired
    LogRecordDao logRecordDao;

    @Autowired
    DeviceDao deviceDao;

    @Autowired
    MailQueueDao mailQueueDao;
    @Autowired
    UserDao userDao;

    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private TaskService taskService;

    @Autowired
    IdentityService identityService;

    @Autowired
    HistoryService historyService;

    @RequestMapping(value = "/getOngoingProcessStaterByMe", method = RequestMethod.POST)
    public Map<String, Object> getOngoingProcessStaterByMe(HttpServletRequest httpServletRequest,
                                                           @RequestParam("pageNum") int pageNum,
                                                           @RequestParam("pageSize") int pageSize) {
        // pageNum从0开始
        int offset = pageNum * pageSize;
        User u = (User) httpServletRequest.getAttribute("login");

        // 查询所有正在进行的流程
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                .variableValueEquals("processStatus", "进行中").orderByProcessInstanceId().desc().list();

        List<ProcessModel> processModels = this.getProcessListByVariable(variableInstances, u);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", processModels.size());
        resultMap.put("row", processModels.subList(offset, Math.min(offset + pageSize, processModels.size())));

        return resultMap;
    }

    @PostMapping("/cancelProcess")
    public String cancelProcess(HttpServletRequest httpServletRequest) {
        String processInstanceId = httpServletRequest.getParameter("processId");
        User u = (User) httpServletRequest.getAttribute("login");

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

        if (processInstance == null) {
            throw new RuntimeException("不存在该流程实例ID: " + processInstanceId);
        }

        // activity
        runtimeService.setVariable(processInstanceId, "processStatus", "已取消");
        runtimeService.deleteProcessInstance(processInstanceId, u.getUserName() + " - 取消");

        // 业务 更新在库
        String businessKey = processInstance.getBusinessKey();
        String processDefinitionName = processInstance.getProcessDefinitionName();

        List<LogRecord> logRecordList = logRecordDao.findByBusinessKey(businessKey);

        for (LogRecord logRecord : logRecordList) {
            Device device = deviceDao.findByDeviceId(logRecord.getDeviceId());
            if (device == null) {
                log.error("找不到该设备ID：" + logRecord.getDeviceId());
                continue;
            }
            device.setStatus(DeviceStatus.IN_STORE.getValue());
            device.setUpdateDate(new Date());
            deviceDao.saveAndFlush(device);

            // 日志
            LogRecord logRecord1 = new LogRecord();

            logRecord1.setDeviceId(logRecord.getDeviceId());
            logRecord1.setDeviceName(logRecord.getDeviceName());
            logRecord1.setDevicePath(logRecord.getDevicePath());
            logRecord1.setBusinessKey(businessKey);
            logRecord1.setRemark("取消");
            logRecord1.setOperatorDate(new Date());
            logRecord1.setOperatorId(u.getUserId());
            logRecord1.setOperatorName(u.getUserName());
            logRecord1.setLogType(processDefinitionName.equalsIgnoreCase("报废流程") ? LogRecordType.LOG_TYPE_SCRAP_CANCEL
                    : LogRecordType.LOG_TYPE_DELETE_CANCEL);

            logRecordDao.save(logRecord1);

        }

        return "取消流程成功";

    }
    @RequestMapping(value = "/getFinishedProcessStaterByMe", method = RequestMethod.POST)
    public Map<String, Object> getFinishedProcessStaterByMe(HttpServletRequest httpServletRequest,
                                                            @RequestParam("pageNum") int pageNum,
                                                            @RequestParam("pageSize") int pageSize) {

        int offset = pageNum * pageSize;
        User u = (User) httpServletRequest.getAttribute("login");

        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                .variableValueNotEquals("processStatus", "进行中").orderByProcessInstanceId().desc()
                .list();

        List<ProcessModel> processModels = this.getProcessListByVariable(variableInstances, u);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", processModels.size());
        resultMap.put("row", processModels.subList(offset, Math.min(offset + pageSize, processModels.size())));

        return resultMap;
    }

    @RequestMapping(value = "/getOngoingTask", method = RequestMethod.POST)
    public Map<String, Object> getOngoingTask(HttpServletRequest httpServletRequest,
                                              @RequestParam("pageNum") int pageNum,
                                              @RequestParam("pageSize") int pageSize) {

        User u = (User) httpServletRequest.getAttribute("login");
        int offset = pageNum * pageSize;

        // 候选人或者办理人
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(String.valueOf(u.getUserId()))
                .orderByTaskCreateTime().desc().listPage(offset, pageSize);

        List<Task> totalTaskList = taskService.createTaskQuery()
                .taskCandidateUser(String.valueOf(u.getUserId())).list();

        List<TaskModel> taskModelList = new ArrayList<>();

        for (Task task : tasks) {
            // 获取业务key
            String processInstanceId = task.getProcessInstanceId();
            String businessKey = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult().getBusinessKey();

            TaskModel taskModel = new TaskModel();
            taskModel.setTaskId(task.getId());
            taskModel.setTaskName(task.getName());
            taskModel.setCreateTime(task.getCreateTime());
            // 任务没有领取的情况下时长为0
            taskModel.setDuration(null);
            taskModel.setStatus("进行中");
            taskModel.setBusinessKey(businessKey);

            taskModelList.add(taskModel);

            log.info("任务id " + task.getId() + ", 任务名称 " + task.getName() +
                    ", 任务创建时间 " + task.getCreateTime() + ", 业务key: " + businessKey);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", totalTaskList.size());
        resultMap.put("row", taskModelList);

        return resultMap;

    }

    @RequestMapping(value = "/getFinishTask", method = RequestMethod.POST)
    public Map<String, Object> getFinishTask(HttpServletRequest httpServletRequest,
                                              @RequestParam("pageNum") int pageNum,
                                              @RequestParam("pageSize") int pageSize) {

        User u = (User) httpServletRequest.getAttribute("login");
        int offset = pageNum * pageSize;

        // 先获取完成的任务
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .finished().taskAssignee(u.getUserId().toString()).orderByTaskCreateTime().desc().list();

        List<TaskModel> taskModelList = new ArrayList<>();

        for (HistoricTaskInstance task : list) {
            // 根据已经完成的任务ID查询流程实例ID, 再查找已完成的variableInstance
            String processInstanceId = task.getProcessInstanceId();
            // 如果是Manager自己申请自己取消的话, 这里会把<已取消>的数据加进来, 感觉也可以. 自己取消自己申请的任务可以当作<已完成>
            HistoricVariableInstance variableInstance = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .variableValueNotEquals("processStatus", "进行中").singleResult();

            if (variableInstance != null) {
                TaskModel taskModel = new TaskModel();

                // 这里要用history service查询businessKey
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId).singleResult();

                if (historicProcessInstance != null) {
                    taskModel.setTaskId(task.getId());
                    taskModel.setTaskName(task.getName());
                    taskModel.setCreateTime(task.getCreateTime());
                    taskModel.setEndTime(task.getEndTime());
                    taskModel.setDuration(task.getDueDate() == null ? null : task.getDueDate().getTime() / (1000 * 60));
                    taskModel.setStatus(variableInstance.getValue().toString());
                    taskModel.setBusinessKey(historicProcessInstance.getBusinessKey());

                    taskModelList.add(taskModel);

                    log.info(String.join(",",
                            "Task ID: " + task.getId(),
                            "流程实例ID: " + task.getProcessInstanceId(),
                            "Task Name: " + task.getName(),
                            "Assignee: " + task.getAssignee(),
                            "Start Time: " + task.getStartTime(),
                            "End Time: " + task.getEndTime(),
                            "Duration: " + task.getDurationInMillis() + " milliseconds",
                            "状态 : " + variableInstance.getValue()));
                }
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", taskModelList.size());
        resultMap.put("row", taskModelList.subList(offset, Math.min(offset + pageSize, taskModelList.size())));

        return resultMap;
    }
    @RequestMapping(value = "/handleTask", method = RequestMethod.POST)
    public String handleTask(HttpServletRequest httpServletRequest) {
        User u = (User) httpServletRequest.getAttribute("login");

        String taskId = httpServletRequest.getParameter("taskId");
        String judge = httpServletRequest.getParameter("judge");
        // 如果是同意的情况不传入reason
        String reason = httpServletRequest.getParameter("reason");

        Map<String, Object> var = new HashMap<>();
        var.put("judge", judge);

        List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(u.getUserId().toString()).list();

        for (Task task : taskList) {
            if (taskId.equalsIgnoreCase(task.getId())) {
                taskService.claim(task.getId(), u.getUserId().toString());

                log.info("任务ID：" + task.getId() + ", " + u.getUserName() + " >> "+ task.getName() +" 拾取成功");

                // 获取业务key
                String processInstanceId = task.getProcessInstanceId();

                // 这个要放在前面, 后面任务完成的话属性设置就会报错
                runtimeService.setVariable(processInstanceId, "processStatus",
                        judge.equalsIgnoreCase("Y") ? "已同意" : "被拒绝");

                String businessKey = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                        .singleResult().getBusinessKey();

                // 更新日志表
                List<LogRecord> logRecordList = logRecordDao.findByBusinessKey(businessKey);

                Integer userId = logRecordList.stream()
                        .map(LogRecord::getOperatorId)
                        .findFirst()
                        // 如果logRecordList为空，会抛出异常
                        .orElseThrow(() -> new NoSuchElementException("logRecordList is empty"));

                User user1 = userDao.findByUserId(userId);

                for (LogRecord logRecord : logRecordList) {
                    LogRecord logRecord1 = new LogRecord();

                    logRecord1.setDeviceId(logRecord.getDeviceId());
                    logRecord1.setDeviceName(logRecord.getDeviceName());
                    logRecord1.setDevicePath(logRecord.getDevicePath());
                    logRecord1.setBusinessKey(businessKey);
                    logRecord1.setRemark(reason);
                    logRecord1.setOperatorDate(new Date());
                    logRecord1.setOperatorId(u.getUserId());
                    logRecord1.setOperatorName(u.getUserName());
                    if (task.getName().equalsIgnoreCase("Manager报废审批")) {
                        logRecord1.setLogType(judge.equalsIgnoreCase("Y") ? LogRecordType.LOG_TYPE_SCRAP_APPROVE :
                                LogRecordType.LOG_TYPE_SCRAP_REJECT);
                    } else {
                        logRecord1.setLogType(judge.equalsIgnoreCase("Y") ? LogRecordType.LOG_TYPE_DELETE_APPROVE :
                                LogRecordType.LOG_TYPE_SCRAP_APPLY);
                    }

                    logRecordDao.save(logRecord1);

                }

                // 邮件
                Context context = new Context();
                context.setVariable("processId", processInstanceId);
                context.setVariable("handleUser", u.getUserName());
                context.setVariable("result", judge.equalsIgnoreCase("Y") ? "通过" : "拒绝");

                MailQueue mailQueue = new MailQueue();

                mailQueue.setFlag(0);
                mailQueue.setMailTo(user1.getMail());
                mailQueue.setMailFrom(u.getMail());
                if (task.getName().equalsIgnoreCase("Manager报废审批")) {
                    mailQueue.setSubject("报废审批结果通知");
                    mailQueue.setEmailInfo(templateEngine.process("scrapResult", context));
                } else {
                    mailQueue.setSubject("删除审批结果通知");
                    mailQueue.setEmailInfo(templateEngine.process("deleteResult", context));
                }

                mailQueueDao.save(mailQueue);

                taskService.complete(task.getId(), var);
                log.info("任务ID：" + task.getId() + " 审批完成 .");
            }
        }

        return "操作成功";
    }

    private List<ProcessModel> getProcessListByVariable(List<HistoricVariableInstance> variableInstances, User u) {

        List<ProcessModel> resultList = new ArrayList<>();
        // 查询发起者名下的流程实例ID
        for (HistoricVariableInstance variableInstance : variableInstances) {
            String processInstanceId = variableInstance.getProcessInstanceId();
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                    .startedBy(String.valueOf(u.getUserId()))
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (processInstance != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedStartTime = dateFormat.format(processInstance.getStartTime());
                String formattedEndTime = processInstance.getEndTime() == null ? "" :
                        dateFormat.format(processInstance.getEndTime());
                String durationInMinutes = processInstance.getDurationInMillis() == null ? "-" :
                        String.valueOf(processInstance.getDurationInMillis() / (1000 * 60));

                ProcessModel processModel = new ProcessModel();
                processModel.setProcessId(Integer.valueOf(processInstance.getId()));
                processModel.setProcessName(processInstance.getProcessDefinitionName());
                processModel.setBusinessKey(processInstance.getBusinessKey());
                processModel.setStartTime(processInstance.getStartTime());
                processModel.setEndTime(processInstance.getEndTime());
                processModel.setDuration(durationInMinutes);
                processModel.setStatus(variableInstance.getValue());
                processModel.setDeleteReason(processInstance.getDeleteReason());

                resultList.add(processModel);

                log.info("流程实例ID: " + processInstance.getId() +
                        ", 流程名称:" + processInstance.getProcessDefinitionName() +
                        ", 业务Key(详情): " + processInstance.getBusinessKey() +
                        ", 流程开始时间: " + formattedStartTime +
                        ", 流程结束时间: " + formattedEndTime +
                        ", 时长: " + durationInMinutes + " minutes" +
                        ", 取消理由： " + processInstance.getDeleteReason() +
                        ", 状态: " + variableInstance.getValue());

            }
        }

        resultList.sort(Comparator.comparing(ProcessModel::getProcessId, Comparator.reverseOrder()));

        return resultList;
    }

    // 在权限有admin_menu_process2时用到的接口
    @RequestMapping(value = "/getAllHistoryProcess", method = RequestMethod.POST)
    public Map<String, Object> getAllHistoryProcess(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {

        int offset = pageNum * pageSize;
        List<HistoricProcessInstance> HistoricProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceStartTime().desc().list();

        List<ProcessModel> resultList = new ArrayList<>();
        for (HistoricProcessInstance processInstance : HistoricProcessInstance) {
            if (processInstance != null) {
                HistoricVariableInstance variableInstance = historyService.createHistoricVariableInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .variableName("processStatus").singleResult();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String durationInMinutes = processInstance.getDurationInMillis() == null ? "-" :
                        String.valueOf(processInstance.getDurationInMillis() / (1000 * 60));

                ProcessModel processModel = new ProcessModel();
                processModel.setProcessId(Integer.valueOf(processInstance.getId()));
                processModel.setProcessName(processInstance.getProcessDefinitionName());
                processModel.setBusinessKey(processInstance.getBusinessKey());
                processModel.setStartTime(processInstance.getStartTime());
                processModel.setEndTime(processInstance.getEndTime());
                processModel.setDuration(durationInMinutes);
                processModel.setStatus(variableInstance != null ? variableInstance.getValue() : "N/A");

                User user = userDao.findByUserId(Integer.valueOf(processInstance.getStartUserId()));
                processModel.setStater(user == null ? "" : user.getUserName());

                resultList.add(processModel);

            }

        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", resultList.size());
        resultMap.put("row", resultList.subList(offset, Math.min(offset + pageSize, resultList.size())));

        return resultMap;
    }

    @PostMapping(value = "/getBusinessDetail")
    public List<LogRecordModel> getBusinessDetail(HttpServletRequest httpServletRequest) {
        String businessKey = httpServletRequest.getParameter("businessKey");

        return logRecordDao.findBusinessDetail(Sort.by(Sort.Direction.DESC, "id"), businessKey);
    }
}
