package com.asv;


import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
public class TestProcess {
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

    @Test
    public void testDemo(){
        System.out.println("test");
    }

    @Test
    public void testStartProcess(){
        // 如果下一个任务执行失败会停在当前任务，需要重新完成当前任务.
        //       根据流程定义Key启动流程
        // 设置startUser
        identityService.setAuthenticatedUserId("wangwu2");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("scrapKey");
//        输出内容
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
//        System.out.println("当前活动Id：" + processInstance.getActivityId());



        Task task1 = taskService.createTaskQuery().processDefinitionKey("scrapKey").
                taskAssignee("wangwu2").singleResult();

        System.out.println("任务id " + task1.getId());
        System.out.println("任务名称 " + task1.getName());
        System.out.println("任务处理人 " + task1.getAssignee());
        System.out.println("任务创建时间 " + task1.getCreateTime());

        taskService.complete(task1.getId());
        System.out.println("完成taskID: " + task1.getId());

    }

    @Test
    public void testMailProcess(){
//        根据流程定义Id启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("mailKey");
//        输出内容
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }

    @Test
    public void testExecuteMail(){

        ProcessInstance pi = runtimeService.startProcessInstanceByKey("scrapKey");

        Job job = managementService.createJobQuery().executionId(pi.getId()).singleResult();

        System.out.println("定时任务 " + job.getId());

        managementService.executeJob(job.getId());

    }


    @Test
    public void testGetMyTask(){
        TaskQuery taskQuery = taskService.createTaskQuery().
                processDefinitionKey("scrapKey").taskCandidateUser("lisi");

//        TaskQuery taskQuery = taskService.createTaskQuery().
//                processDefinitionKey("scrapKey").taskAssignee("wangwu2");
        List<Task> taskList = taskQuery.list();

        if (taskList != null) {
            for (Task task : taskList) {
                System.out.println("任务id " + task.getId());
                System.out.println("任务名称 " + task.getName());
                System.out.println("任务处理人 " + task.getAssignee());
                System.out.println("任务创建时间 " + task.getCreateTime());

            }

        }

    }

    @Test
    public void claimMyTask(){
        TaskQuery taskQuery = taskService.createTaskQuery() // 候选人或者办理人
                .taskCandidateOrAssigned("lisi")
                .orderByTaskCreateTime().asc();

        List<Task> tasks = taskQuery.listPage(0, 10);
        for (Task task : tasks) {
            System.out.println("任务id " + task.getId());
            System.out.println("任务名称 " + task.getName());
            System.out.println("任务处理人 " + task.getAssignee());
            System.out.println("任务拥有人 " + task.getOwner());
            System.out.println("任务创建时间 " + task.getCreateTime());

        }




//        Task task = taskService.createTaskQuery().
//                processDefinitionKey("scrapKey").taskCandidateUser("lisi").singleResult();
//
//        if (null != task) {
//            taskService.claim(task.getId(), "lisi");
//        }
//        assert task != null;
//        System.out.println(task.getId() + " -- 任务拾取成功");
//
//        String judge = "Y";
//        Map<String, Object> var = new HashMap<>();
//        var.put("judge", judge);
//
//        // 这里可以设置发送的邮件内容
//        taskService.complete(task.getId(), var);

    }

    @Test
    public void TestCancelProcess() {
         // 流程实例id：5001
        runtimeService.deleteProcessInstance("5001", "不想玩啦");

    }

    @Test
    public void TestGetMyApply() {
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .startedBy("wangwu2")
                .list();

        for (HistoricProcessInstance processInstance : historicProcessInstances) {
            System.out.println("Process Instance ID: " + processInstance.getId());
            System.out.println("Business Key: " + processInstance.getBusinessKey());
            System.out.println("Process Definition ID: " + processInstance.getProcessDefinitionId());
            System.out.println("Start Time: " + processInstance.getStartTime());
            System.out.println("End Time: " + processInstance.getEndTime());
            System.out.println("Duration: " + processInstance.getDurationInMillis() + " milliseconds");
            System.out.println("Start User ID: " + processInstance.getStartUserId());
            System.out.println("Start Activity ID: " + processInstance.getStartActivityId());
            // 其他流程实例信息的打印
        }
    }

    @Test
    public void TestCompleteTask() {
        taskService.complete("150008");
        taskService.complete("152508");
        taskService.complete("155008");

    }

    @Test
    public void testDeleteDeployment(){
        List<ProcessDefinition> scrapKey1 = repositoryService.createProcessDefinitionQuery().processDefinitionKey("scrapKey").list();

        for (ProcessDefinition processDefinition : scrapKey1) {
            repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
        }

        // 级联删除
//        repositoryService.deleteDeployment(scrapKey.getDeploymentId(), true);

    }

//    @Test
    public void testSendMail(){
        // 创建Email对象
        Email email = new SimpleEmail();
        email.setHostName("smtp.163.com");
        email.setSmtpPort(994);
        email.setAuthentication("m15268837061@163.com", "OXQCKVNELQHKBPPV");
        email.setSSLOnConnect(true); // 如果需要使用SSL连接，请启用此选项

        try {
            // 设置邮件相关信息
            email.setFrom("m15268837061@163.com");
            email.addTo("812647742@qq.com");
            email.setSubject("Test Email");
            email.setMsg("This is a test email.");

            // 发送邮件
            email.send();
        } catch (EmailException e) {
            // 处理邮件发送异常
            e.printStackTrace();
        }

    }

}
