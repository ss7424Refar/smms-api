package com.asv.service;

import com.asv.constant.DeviceStatus;
import com.asv.dao.DeviceDao;
import com.asv.dao.LogRecordDao;
import com.asv.entity.Device;
import com.asv.entity.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("DeviceService")
@Slf4j
public class DeviceService {
    @Autowired
    LogRecordDao logRecordDao;
    @Autowired
    DeviceDao deviceDao;

    public void update(DelegateExecution execution){
        String judge = execution.getVariable("judge").toString();
        // 获取deviceIds
        Set<String> deviceIds = this.getDeviceIds(execution);

        for (String s : deviceIds) {

            Device device = deviceDao.findByDeviceId(s);
            // 同意 -> 已报废
            if (judge.equalsIgnoreCase("Y")) {
                device.setStatus(DeviceStatus.SCRAPPED.getValue());
                device.setScrapDate(new Date());
            } else {
                device.setStatus(DeviceStatus.IN_STORE.getValue());
            }

            device.setUpdateDate(new Date());
            deviceDao.saveAndFlush(device);
            log.info("deviceID: " + s + " >> " + judge + " 报废流程结束");
        }

    }

    public void update2(DelegateExecution execution){

        String judge = execution.getVariable("judge").toString();
        System.out.println("--" + execution.getEventName() + execution.getVariable("processStatus"));

        Set<String> deviceIds = this.getDeviceIds(execution);

        for (String s : deviceIds) {
            Device device = deviceDao.findByDeviceId(s);
            // 同意 -> 已删除
            if (judge.equalsIgnoreCase("Y")) {
                deviceDao.delete(device);
            } else {
                device.setStatus(DeviceStatus.IN_STORE.getValue());
                device.setUpdateDate(new Date());
                deviceDao.saveAndFlush(device);
            }

            log.info("deviceID: " + s + " >> " + judge + " 删除流程结束");
        }

    }

    private Set<String> getDeviceIds(DelegateExecution execution) {
        String businessKey = execution.getProcessInstanceBusinessKey();

        // 获取deviceIds
        List<LogRecord> logRecordList = logRecordDao.findByBusinessKey(businessKey);

        return logRecordList.stream()
                .map(LogRecord::getDeviceId)
                .collect(Collectors.toSet());
    }
}
