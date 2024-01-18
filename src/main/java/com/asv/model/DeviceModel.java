package com.asv.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class DeviceModel {
    @ExcelProperty("编号")
    private String deviceId;

    @ExcelProperty("存储设备名称")
    private String deviceName;

    @ExcelIgnore
    private Integer _status;

    @ExcelIgnore
    private String capacity;

    @ExcelProperty("程序")
    private String program;

    @ExcelIgnore
    private Integer _virus;

    @ColumnWidth(20)
    @ExcelProperty("杀毒时间")
    private Date virusDate;

    @ExcelProperty("领用人")
    private String currentUserName;

    @ExcelIgnore
    private String departName;

    @ExcelIgnore
    private String sectionName;

    @ColumnWidth(20)
    @ExcelProperty("领用日期")
    private Date borrowDate;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("杀毒")
    private String virus;

    @ExcelIgnore
    private Integer departId;

    @ExcelIgnore
    private Integer sectionId;

    @ExcelProperty("线别")
    private String usingLine;

    @ExcelProperty("班别")
    private String usingPost;

    @ExcelIgnore
    private String pnDevice;
//    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")

    @ColumnWidth(20)
    @ExcelProperty("入库日期")
    private Date storeDate;

    @ExcelIgnore
    private String type;

    @ColumnWidth(20)
    @ExcelProperty("报废日期")
    private Date scrapDate;

    @ExcelProperty("备注")
    private String remark;

    public DeviceModel(String deviceId, String deviceName, Integer _status, String capacity, String program,
                       Integer _virus, Date virusDate, String currentUserName, String departName, String sectionName,
                       Date borrowDate, String remark, String usingLine, String usingPost, String pnDevice,
                       Date storeDate, String type, Date scrapDate) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this._status = _status;
        this.capacity = capacity;
        this.program = program;
        this._virus = _virus;
        this.virusDate = virusDate;
        this.currentUserName = currentUserName;
        this.departName = departName;
        this.sectionName = sectionName;
        this.borrowDate = borrowDate;
        this.remark = remark;
        this.usingLine = usingLine;
        this.usingPost = usingPost;
        this.pnDevice = pnDevice;
        this.storeDate = storeDate;
        this.type = type;
        this.scrapDate = scrapDate;
    }

    // getDeviceUpdateInfo
    public DeviceModel(String deviceId, String deviceName, String capacity, String program, Integer departId,
                       Integer sectionId, String remark) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.capacity = capacity;
        this.program = program;
        this.departId = departId;
        this.sectionId = sectionId;
        this.remark = remark;
    }

}
