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
@Table(name = "t_borrow_history")
public class BorrowHistory {
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
    @ExcelProperty("领用人")
    private String userName;

    @Column(name = "using_line", columnDefinition = "varchar(8)")
    @ExcelProperty("线别")
    private String usingLine;

    @Column(name = "user_post", columnDefinition = "varchar(8)")
    @ExcelProperty("班别")
    private String userPost;

    @Column(name = "start_time")
    @ExcelProperty("借用开始时间")
    @ColumnWidth(20)
    private Date startTime;

    @Column(name = "end_time")
    @ExcelProperty("借用结束时间")
    @ColumnWidth(20)
    private Date endTime;

    @Column(name = "remark")
    @ExcelProperty("备注")
    private String remark;
}
