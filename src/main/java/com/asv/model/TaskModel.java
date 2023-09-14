package com.asv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskModel {
    private String taskId;
    private String taskName;
    private Date createTime;
    private Date endTime;
    private Long duration;
    private String status;
    private String businessKey;
}
