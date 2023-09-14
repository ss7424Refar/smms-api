package com.asv.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceModel {
    private String deviceId;
    private String deviceName;
    private Integer _status;
    private String capacity;
    private String program;
    private Integer _virus;
    private Date virusDate;
    private String currentUserName;
    private String departName;
    private String sectionName;
    private Date borrowDate;

    private String status;
    private String virus;

    private Integer departId;
    private Integer sectionId;
    private String remark;

    private String usingLine;
    private String usingPost;
    private String pnDevice;
//    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date storeDate;
    private String type;
    private Date scrapDate;

    public DeviceModel(String deviceId, String deviceName, Integer _status, String capacity, String program, Integer _virus, Date virusDate, String currentUserName, String departName, String sectionName, Date borrowDate, String remark, String usingLine, String usingPost, String pnDevice, Date storeDate, String type, Date scrapDate) {
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

    public DeviceModel(String deviceId, String deviceName, String capacity, String program, Integer departId, Integer sectionId, String remark) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.capacity = capacity;
        this.program = program;
        this.departId = departId;
        this.sectionId = sectionId;
        this.remark = remark;
    }

    public DeviceModel(String deviceId, String deviceName, Integer _status, String capacity, String program, Integer _virus, Date virusDate, String currentUserName, String departName, String sectionName, Date borrowDate) {
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
    }

}
