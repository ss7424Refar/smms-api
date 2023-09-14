package com.asv.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "t_log_record")
public class LogRecord {
    // 每个实体类都要指明主键, 否则会报错 No identifier specified for entity
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "business_key")
    private String businessKey;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_path")
    private String devicePath;

    @Column(name = "log_type")
    private String logType;

    @Column(name = "operator_id")
    private Integer operatorId;

    @Column(name = "operator_name")
    private String operatorName;

    @Column(name = "operator_date")
    private Date operatorDate;

    @Column(name = "remark")
    private String remark;

}
