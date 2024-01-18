package com.asv.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
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
    @ExcelIgnore
    private Integer id;

    @Column(name = "business_key", columnDefinition = "varchar(21)")
    @ExcelIgnore
    private String businessKey;

    @Column(name = "device_id", columnDefinition = "varchar(11)")
    @ExcelProperty("设备编号")
    private String deviceId;

    @Column(name = "device_name")
    @ExcelProperty("设备名称")
    private String deviceName;

    @Column(name = "device_path")
    @ExcelIgnore
    private String devicePath;

    @Column(name = "log_type", columnDefinition = "varchar(8)")
    @ExcelProperty("操作类型")
    @ColumnWidth(12)
    private String logType;

    @Column(name = "operator_id")
    @ExcelIgnore
    private Integer operatorId;

    @Column(name = "operator_name", columnDefinition = "varchar(8)")
    @ExcelProperty("操作人")
    private String operatorName;

    @Column(name = "operator_date")
    @ExcelProperty("操作时间")
    @ColumnWidth(20)
    private Date operatorDate;

    @Column(name = "remark")
    @ExcelProperty("备注")
    private String remark;

}
