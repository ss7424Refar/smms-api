package com.asv.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "t_virus_history")
public class VirusHistory {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelIgnore
    private Integer id;

    @Column(name = "device_id", columnDefinition = "varchar(11)")
    @ExcelProperty("设备编号")
    private String deviceId;

    @Column(name = "user_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ExcelIgnore
    private Integer userId;

    @Column(name = "user_name", columnDefinition = "varchar(8)")
    @ExcelProperty("操作者")
    private String userName;

    @Column(name = "log_path")
    @ExcelIgnore
    private String logPath;

    @Column(name = "return_code", columnDefinition = "varchar(20)")
    @ExcelIgnore
    private String returnCode;

    @Column(name = "code_string", columnDefinition = "varchar(15)")
    @ExcelProperty("杀毒信息")
    @ColumnWidth(30)
    private String codeString;

    @ColumnWidth(20)
    @Column(name = "upload_date")
    @ExcelProperty("杀毒时间")
    private Date uploadDate;

    @Column(name = "remark")
    @ExcelProperty("备注")
    private String remark;
}
