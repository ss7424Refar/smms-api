package com.asv.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogRecordModel {
    private String businessKey;
    private String logType;
    private String operatorName;
    private Date operatorDate;
    private String remark;

    private String deviceId;
    private String deviceName;

    public LogRecordModel(String businessKey, String logType, String operatorName, Date operatorDate, String remark) {
        this.businessKey = businessKey;
        this.logType = logType;
        this.operatorName = operatorName;
        this.operatorDate = operatorDate;
        this.remark = remark;
    }

    public LogRecordModel(String deviceId, String deviceName, String logType, String operatorName, Date operatorDate, String remark) {
        this.logType = logType;
        this.operatorName = operatorName;
        this.operatorDate = operatorDate;
        this.remark = remark;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }
}
