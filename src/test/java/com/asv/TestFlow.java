package com.asv;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class TestFlow {
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


    // 发起报废申请并完成任务
    @Test
    public void testStartProcess() {
        String stater = "wangwu2";

        identityService.setAuthenticatedUserId(stater);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String businessKey = dateFormat.format(new Date());

        HashMap<String, Object> map = new HashMap<>();
        map.put("assignee", stater);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("scrapKey", businessKey, map);

        Task task1 = taskService.createTaskQuery()
                .processDefinitionKey("scrapKey")
                .taskAssignee(stater)
                .singleResult();

        taskService.complete(task1.getId());
        runtimeService.setVariable(processInstance.getId(), "processStatus", "ongoing");

        System.out.println(String.join(", ",
                "流程实例id：" + processInstance.getId(),
                "流程实例Name：" + processInstance.getProcessDefinitionName(),
                "流程业务Key：" + processInstance.getBusinessKey(),
                "流程开始时间：" + processInstance.getStartTime(),
                "任务id：" + task1.getId(),
                "任务名称：" + task1.getName(),
                "任务处理人：" + task1.getAssignee(),
                "任务创建时间：" + task1.getCreateTime(),
                "完成taskID: " + task1.getId()
        ));
    }

    // 查询我发起的流程 - 获取我的进行中全部流程 (进行中)
    @Test
    public void testGetMyOnGoingProcess() {
        String stater = "wangwu2";
        int pageNo = 0;
        int pageSize = 10;

        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                .variableValueEquals("processStatus", "ongoing").orderByProcessInstanceId().desc()
                .listPage(pageNo, pageSize);

        for (HistoricVariableInstance variableInstance : variableInstances) {
            String processInstanceId = variableInstance.getProcessInstanceId();
            System.out.println(processInstanceId);
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                    .startedBy(stater)
                    .processInstanceId(processInstanceId)
                    .singleResult();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            long durationInMinutes = processInstance.getDurationInMillis() / (1000 * 60);
            String formattedStartTime = dateFormat.format(processInstance.getStartTime());
//            String formattedEndTime = dateFormat.format(processInstance.getEndTime());

            System.out.println("流程实例ID: " + processInstance.getId() +
                    ", 流程名称:" + processInstance.getProcessDefinitionName() +
                    ", 业务Key(详情): " + processInstance.getBusinessKey() +
                    ", 流程开始时间: " + formattedStartTime +
                    ", 流程结束时间: " + "-" +
                    ", 时长: " + "-" + " minutes" +
//                    ", 取消理由： " + processInstance.getDeleteReason() +
                    ", 状态: " + variableInstance.getValue());
        }

    }

    // 查询我发起的流程 - 获取我的已结束全部流程 (/被拒绝/已取消/已结束)
    @Test
    public void testGetMyFinishedProcess() {
        String stater = "wangwu2";
        int pageNo = 0; // offset
        int pageSize = 10; // limit

//        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
//                .startedBy(stater).finished().orderByProcessInstanceStartTime().desc().listPage(pageNo, pageSize);

        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                .variableValueNotEquals("processStatus", "ongoing").orderByProcessInstanceId().desc()
                .listPage(pageNo, pageSize);

        for (HistoricVariableInstance variableInstance : variableInstances) {
            String processInstanceId = variableInstance.getProcessInstanceId();

            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                    .startedBy(stater)
                    .processInstanceId(processInstanceId)
                    .singleResult();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long durationInMinutes = processInstance.getDurationInMillis() / (1000 * 60);
            String formattedStartTime = dateFormat.format(processInstance.getStartTime());
            String formattedEndTime = dateFormat.format(processInstance.getEndTime());

            System.out.println("流程实例ID: " + processInstance.getId() +
                    ", 流程名称:" + processInstance.getProcessDefinitionName() +
                    ", 业务Key(详情): " + processInstance.getBusinessKey() +
                    ", 流程开始时间: " + formattedStartTime +
                    ", 流程结束时间: " + formattedEndTime +
                    ", 时长: " + durationInMinutes + " minutes" +
                    ", 取消理由： " + processInstance.getDeleteReason() +
                    ", 状态: " + variableInstance.getValue());
        }
    }


    // 取消申请 - 根据上面查询到的Process Instance ID 进行取消
    @Test
    public void testCancelProcess() {
        String reason = "有待前端输入";
        reason = "cancel:" + reason;
        String processInstanceId = "110001";

        runtimeService.setVariable(processInstanceId, "processStatus", "cancel");
        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }


    // Manager审批 - 查看Manager名下的任务
    @Test
    public void testNeedManagerApproveTask() {
        String taskOwner = "zhangsan";
        int pageNo = 0;
        int pageSize = 10;

        // 候选人或者办理人
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateUser(taskOwner)
                .orderByTaskCreateTime().desc();

        List<Task> tasks = taskQuery.listPage(pageNo, pageSize);
        for (Task task : tasks) {
            // 获取业务key
            String processInstanceId = task.getProcessInstanceId();
            String businessKey = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getBusinessKey();

            System.out.println("任务id " + task.getId() + ", 任务名称 " + task.getName() + ", 任务创建时间 " + task.getCreateTime() + ", 业务key为: " + businessKey);
        }

    }

    // 查询manager下的历史任务
    @Test
    public void testGetManagerHistory() {
        String taskOwner = "zhangsan";
        // 这里查询已完成的
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().finished().taskAssignee(taskOwner).list();

        for (HistoricTaskInstance task : list) {

            HistoricVariableInstance variableInstance = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).
                    variableValueNotEquals("processStatus", "ongoing").singleResult();

            if (variableInstance != null) {
                System.out.println(String.join(",",
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

    // Manager领取任务审批 - 同意
    @Test
    public void testManagerApprove() {
        String taskId = "180014";
        String judge = "Y";
        String taskOwner = "lisi";

        Map<String, Object> var = new HashMap<>();
        var.put("judge", judge);

        List<Task> taskList = taskService.createTaskQuery().
                processDefinitionKey("scrapKey").taskCandidateUser(taskOwner).list();

        for (Task task : taskList) {
            if (taskId.equalsIgnoreCase(task.getId())) {
                taskService.claim(task.getId(), taskOwner);

                System.out.println(task.getId() + taskOwner + " -- 任务拾取成功");

                // 这个要放在前面, 后面任务完成的话属性设置就会报错
                runtimeService.setVariable(task.getProcessInstanceId(), "processStatus", "approved");
                taskService.complete(task.getId(), var);

            }
        }
    }

    // Manager审批 - 拒绝
    @Test
    public void testManagerReject() {
        String taskId = "190014";
        String judge = "N";
        String taskOwner = "zhangsan";
        String reason = "有待前端输入";
        reason = "reject:" + reason;

        Map<String, Object> var = new HashMap<>();
        var.put("judge", judge);

        List<Task> taskList = taskService.createTaskQuery().
                processDefinitionKey("scrapKey").taskCandidateUser(taskOwner).list();

        for (Task task : taskList) {
            if (taskId.equalsIgnoreCase(task.getId())) {
                taskService.claim(task.getId(), taskOwner);

                System.out.println(task.getId() + taskOwner + " -- 任务拾取成功");
                runtimeService.setVariable(task.getProcessInstanceId(), "processStatus", "reject");
                taskService.complete(task.getId(), var);

            }
        }

    }

    // 查询未完成的历史流程
    // 可以有完成和未完成的
    @Test
    public void testHistoryProcess() {
        List<HistoricProcessInstance> HistoricProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .unfinished().list();

        for (HistoricProcessInstance processInstance : HistoricProcessInstance) {
            System.out.println("流程实例ID: " + processInstance.getId());

            String startUserId = processInstance.getStartUserId();
            System.out.println("发起人: " + startUserId);

            Date startTime = processInstance.getStartTime();
            System.out.println("开始时间: " + startTime);
            System.out.println("--------------------------------------");

        }

    }


    // 以管理员的身份查看所有流程
    @Test
    public void testGetAll() {
        // 先查看正在运行的
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .active()
                .list();


        // 已经完成的
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .finished()
                .list();

        for (ProcessInstance processInstance : processInstances) {
            System.out.println("流程实例ID: " + processInstance.getId());

            String startUserId = processInstance.getStartUserId();
            System.out.println("发起人: " + startUserId);

            Date startTime = processInstance.getStartTime();
            System.out.println("开始时间: " + startTime);
        }
        for (HistoricProcessInstance processInstance : historicProcessInstances) {
            System.out.println("流程实例ID: " + processInstance.getId());

            String startUserId = processInstance.getStartUserId();
            System.out.println("发起人: " + startUserId);

            Date startTime = processInstance.getStartTime();
            System.out.println("开始时间: " + startTime);
        }

    }


}
