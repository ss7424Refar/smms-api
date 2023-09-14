package com.asv.listener;

import com.asv.utils.PermissionUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ProcessTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {

        log.info(delegateTask.getName() + " - 任务ID: " + delegateTask.getId());

        if (delegateTask.getName().equals("Manager报废审批") && delegateTask.getEventName().equals("create")) {
            // 测试代码
//            List<String> list = new ArrayList<String>();
//            list.add("lisi");
//            list.add("zhangsan");
//            delegateTask.addCandidateUsers(list);
             delegateTask.addCandidateUsers(PermissionUtil.getManagerUserIds());

        }

        if (delegateTask.getName().equals("Manager删除审批") && delegateTask.getEventName().equals("create")) {
            delegateTask.addCandidateUsers(PermissionUtil.getManagerUserIds());

        }
    }


}
