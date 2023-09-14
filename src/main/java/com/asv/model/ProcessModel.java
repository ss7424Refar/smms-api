package com.asv.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessModel {
    private Integer processId;
    private String processName;
    private String businessKey;
    private Date startTime;
    private Date endTime;
    private String duration;
    private Object status;
    private String deleteReason;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stater;
}
