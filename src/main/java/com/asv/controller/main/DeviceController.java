package com.asv.controller.main;

import com.alibaba.fastjson.JSONObject;
import com.asv.constant.AntivirusStatus;
import com.asv.constant.DeviceStatus;
import com.asv.dao.BorrowHistoryDao;
import com.asv.dao.DeviceDao;
import com.asv.dao.LogRecordDao;
import com.asv.dao.VirusHistoryDao;
import com.asv.entity.*;
import com.asv.http.ResponseResult;
import com.asv.model.DeviceModel;
import com.asv.model.LogRecordModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/device")
public class DeviceController {
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    BorrowHistoryDao borrowHistoryDao;
    @Autowired
    VirusHistoryDao virusHistoryDao;
    @Autowired
    LogRecordDao logRecordDao;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(HttpServletRequest httpServletRequest, @RequestBody JSONObject json) throws ParseException {
        // 去掉转义的
        String pnDevice = json.getString("pnDevice");
        Device device = deviceDao.findByPnDevice(pnDevice);
        if (device != null) {
            throw new RuntimeException("添加失败(存在设备实际路径)");
        }
        Boolean sd = json.getBoolean("SD");
        String type = json.getString("type");
        String prefix = "";

        if (sd) {
            prefix = "TFC-";
        } else {
            if (type.equalsIgnoreCase("Removable Media")) {
                prefix = "USB-";
            } else if (type.equalsIgnoreCase("External hard disk media")) {
                prefix = "HDD-";
            } else {
                prefix = "NTY-";
            }
        }

        User u = (User) httpServletRequest.getAttribute("login");

        // 查找总数
        Long maxId = deviceDao.findMaxId();
        String deviceId = prefix + String.format("%07d", maxId == null ? 1 : maxId + 1);

        Device device1 = Device.builder().name(json.getString("name"))
                .status(DeviceStatus.IN_STORE.getValue())
                .virus(AntivirusStatus.NOT_SCANNED.getValue())
                .type(type)
                .capacity(json.getString("capacity"))
                .program(json.getString("program"))
                .departId(json.getInteger("departId"))
                .sectionId(json.getInteger("sectionId"))
                .pnDevice(pnDevice)
                .deviceId(deviceId)
                .remark(json.getString("remark"))
                .storeDate(new Date())
                .storeUserId(u.getUserId())
                .build();

        deviceDao.save(device1);
        return "添加成功:" + deviceId;
    }

    @RequestMapping(value = "/checkUsingDevice", method = RequestMethod.POST)
    public JSONObject checkUsingDevice(HttpServletRequest httpServletRequest) {
        String deviceId = httpServletRequest.getParameter("deviceId");

        Device device = deviceDao.findByStatusAndDeviceId(DeviceStatus.USING.getValue(), deviceId);

        if (device == null) {
            throw new RuntimeException("不存在该设备ID或状态不为使用中 - " + deviceId);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", device.getDeviceId());
        jsonObject.put("devicePath", device.getPnDevice());

        return jsonObject;
    }

    @PostMapping(value = "/select")
    public Map<String, Object> selectDevice(@RequestParam(value = "deviceId", required = false) String deviceId,
                                            @RequestParam(value = "deviceName", required = false) String deviceName,
                                            @RequestParam(value = "status", required = false) Integer status,
                                            @RequestParam(value = "capacity", required = false) String capacity,
                                            @RequestParam(value = "type", required = false) String type,
                                            @RequestParam(value = "program", required = false) String program,
                                            @RequestParam(value = "departId", required = false) Integer departId,
                                            @RequestParam(value = "sectionId", required = false) Integer sectionId,
                                            @RequestParam(value = "virus", required = false) Integer virus,
                                            @RequestParam(value = "currentUserName", required = false) String currentUserName,
                                            @RequestParam(value = "remark", required = false) String remark,
                                            @RequestParam("pageNum") int pageNum,
                                            @RequestParam("pageSize") int pageSize) {

        Pageable pageable = PageRequest.of(pageNum, pageSize);
        if (ObjectUtils.isEmpty(deviceId)) {
            deviceId = null;
        }
        if (ObjectUtils.isEmpty(deviceName)) {
            deviceName = null;
        }
        if (ObjectUtils.isEmpty(capacity)) {
            capacity = null;
        }
        if (ObjectUtils.isEmpty(type)) {
            type = null;
        }
        if (ObjectUtils.isEmpty(program)) {
            program = null;
        }
        if (ObjectUtils.isEmpty(currentUserName)) {
            currentUserName = null;
        }
        if (ObjectUtils.isEmpty(remark)) {
            remark = null;
        }

        Page<DeviceModel> page = deviceDao.searchDevicesByKeyword(deviceId, deviceName, status, capacity, type, program, departId, sectionId, virus, currentUserName, remark, pageable);
//        Page<DeviceModel> page = deviceDao.searchDevicesByKeyword(deviceId, deviceName, status, capacity, type, program, pageable);

        List<DeviceModel> list = page.getContent();

        list.forEach(deviceModel -> {
            if (deviceModel.get_status() == null || deviceModel.get_virus() == null) {
                throw new RuntimeException("枚举类型空指针错误 !");
            }
            deviceModel.setStatus(DeviceStatus.getDescriptionByValue(deviceModel.get_status()));
            deviceModel.setVirus(AntivirusStatus.getDescriptionByValue(deviceModel.get_virus()));
        });

        Map<String, Object> map = new HashMap<>();
        map.put("rows", list);
        map.put("total", page.getTotalElements());

        return map;
    }

    @PostMapping(value = "/getUpdateInfo")
    public ResponseResult getUpdateInfo(HttpServletRequest httpServletRequest) {
        DeviceModel deviceModel = deviceDao.getDeviceUpdateInfo(httpServletRequest.getParameter("deviceId"));
        if (null == deviceModel) {
            throw new RuntimeException("没有该设备");
        }

        return ResponseResult.success(deviceModel);
    }

    @PostMapping(value = "/update")
    public String updateDevice(@Valid @RequestBody Device device) {

        Device device1 = deviceDao.findByDeviceId(device.getDeviceId());

        if (null == device1) {
            throw new RuntimeException("没有该设备");
        }

        device1.setName(device.getName());
        device1.setCapacity(device.getCapacity());
        device1.setProgram(device.getProgram());
        device1.setDepartId(device.getDepartId());
        device1.setSectionId(device.getSectionId());
        device1.setRemark(device.getRemark());

        deviceDao.saveAndFlush(device1);
        return "更新设备成功";
    }

    @PostMapping(value = "/getDetail")
    public ResponseResult getDeviceDetail(HttpServletRequest httpServletRequest) {
        DeviceModel deviceModel = deviceDao.getDeviceDetail(httpServletRequest.getParameter("deviceId"));

        if (null == deviceModel) {
            throw new RuntimeException("没有该设备");
        }
        if (deviceModel.get_status() == null || deviceModel.get_virus() == null) {
            throw new RuntimeException("枚举类型空指针错误 !");
        }
        deviceModel.setStatus(DeviceStatus.getDescriptionByValue(deviceModel.get_status()));
        deviceModel.setVirus(AntivirusStatus.getDescriptionByValue(deviceModel.get_virus()));

        return ResponseResult.success(deviceModel);
    }

    @PostMapping(value = "/getBorrowDetail")
    public List<BorrowHistory> getBorrowDetail(HttpServletRequest httpServletRequest) {
        Device device = deviceDao.findByDeviceId(httpServletRequest.getParameter("deviceId"));

        if (null == device) {
            throw new RuntimeException("没有该设备");
        }
        List<BorrowHistory> borrowHistoryList = borrowHistoryDao.findAll(Sort.by(Sort.Direction.DESC, "id"));

        // 为了避免看到用户id
        return borrowHistoryList.stream()
                .peek(bh -> bh.setUserId(null))
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/getVirusDetail")
    public List<VirusHistory> getVirusDetail(HttpServletRequest httpServletRequest) {
        Device device = deviceDao.findByDeviceId(httpServletRequest.getParameter("deviceId"));

        if (null == device) {
            throw new RuntimeException("没有该设备");
        }
        List<VirusHistory> virusHistoryList = virusHistoryDao.findAll(Sort.by(Sort.Direction.DESC, "id"));

        // 为了避免看到用户id
        return virusHistoryList.stream()
                .peek(bh -> bh.setUserId(null))
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/getLogDetail")
    public List<LogRecordModel> getLogDetail(HttpServletRequest httpServletRequest) {
        String deviceId = httpServletRequest.getParameter("deviceId");
        Device device = deviceDao.findByDeviceId(deviceId);

        if (null == device) {
            throw new RuntimeException("没有该设备");
        }

        return logRecordDao.findLogDetail(Sort.by(Sort.Direction.DESC, "id"), deviceId);
    }

    @PostMapping(value = "/checkedCsv")
    public void exportChecked(@RequestBody List<String> deviceIdList, HttpServletResponse response) throws IOException {
        deviceIdList.forEach(deviceId -> {
            Device device = deviceDao.findByDeviceId(deviceId);
            if (device == null) {
                throw new RuntimeException("找不到该设备ID - " + deviceId);
            }
        });

        List<DeviceModel> deviceModels = deviceDao.findInDeviceIds(deviceIdList);
        writeToCsv(response, deviceModels);
    }

    @PostMapping(value = "/formCsv")
    public void exportForm(@RequestParam(value = "deviceId", required = false) String deviceId,
                           @RequestParam(value = "deviceName", required = false) String deviceName,
                           @RequestParam(value = "status", required = false) Integer status,
                           @RequestParam(value = "capacity", required = false) String capacity,
                           @RequestParam(value = "type", required = false) String type,
                           @RequestParam(value = "program", required = false) String program,
                           @RequestParam(value = "departId", required = false) Integer departId,
                           @RequestParam(value = "sectionId", required = false) Integer sectionId,
                           @RequestParam(value = "virus", required = false) Integer virus,
                           @RequestParam(value = "currentUserName", required = false) String currentUserName,
                           @RequestParam(value = "remark", required = false) String remark, HttpServletResponse response) throws IOException {

        if (ObjectUtils.isEmpty(deviceId)) {
            deviceId = null;
        }
        if (ObjectUtils.isEmpty(deviceName)) {
            deviceName = null;
        }
        if (ObjectUtils.isEmpty(capacity)) {
            capacity = null;
        }
        if (ObjectUtils.isEmpty(type)) {
            type = null;
        }
        if (ObjectUtils.isEmpty(program)) {
            program = null;
        }
        if (ObjectUtils.isEmpty(currentUserName)) {
            currentUserName = null;
        }
        if (ObjectUtils.isEmpty(remark)) {
            remark = null;
        }

        List<DeviceModel> list = deviceDao.exportByForm(deviceId, deviceName, status, capacity, type, program, departId, sectionId, virus, currentUserName, remark);
        writeToCsv(response, list);

    }


    private void writeToCsv(HttpServletResponse response, List<DeviceModel> list) throws IOException {
        response.setContentType("text/csv; charset=utf-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=deviceInfo_" +
                System.currentTimeMillis() + ".csv");

        PrintWriter writer = response.getWriter();

        // 写入表头
        writer.println("设备编号,设备名称,状态,容量,程序,杀毒,杀毒时间,用户名,部门,课,借出时间,备注,实际路径,入库时间,类型,报废时间");

        list.forEach(deviceModel -> {
            if (deviceModel.get_status() == null || deviceModel.get_virus() == null) {
                throw new RuntimeException("枚举类型空指针错误 !");
            }
            deviceModel.setStatus(DeviceStatus.getDescriptionByValue(deviceModel.get_status()));
            deviceModel.setVirus(AntivirusStatus.getDescriptionByValue(deviceModel.get_virus()));

            String row = deviceModel.getDeviceId() + "," + deviceModel.getDeviceName() + "," + deviceModel.getStatus() + ","
                    + deviceModel.getCapacity() + "," + deviceModel.getProgram() + "," + deviceModel.getVirus() + "," +
                    deviceModel.getVirusDate() + "," + deviceModel.getCurrentUserName() + "," + deviceModel.getDepartName()
                    + "," + deviceModel.getSectionName() + "," + deviceModel.getBorrowDate() + "," +
                    (deviceModel.getRemark() != null ? deviceModel.getRemark() : "")
                    + "," + deviceModel.getPnDevice() + "," + deviceModel.getStoreDate() + "," + deviceModel.getType()
                    + "," + deviceModel.getScrapDate();
            writer.println(row);
        });

        writer.close();
    }

    @PostMapping(value = "/scanReturn")
    public String scanReturn(@RequestBody List<String> deviceIdList) {
        deviceIdList.forEach(deviceId -> {
            Device device = deviceDao.findByDeviceId(deviceId);
            if (device == null) {
                throw new RuntimeException("找不到该设备ID - " + deviceId);
            }
            if (device.getStatus() != DeviceStatus.USING.getValue()) {
                throw new RuntimeException("设备状态不为使用中 - " + deviceId);
            }
        });

        deviceIdList.forEach(deviceId -> {
            Device device = deviceDao.findByDeviceId(deviceId);

            // 在库
            device.setStatus(DeviceStatus.IN_STORE.getValue());
            device.setVirus(AntivirusStatus.SCANNING.getValue());

            device.setUsingPost(null);
            device.setUsingPost(null);
            device.setCurrentUserId(null);
            device.setCurrentUserName(null);
            device.setBorrowDate(null);

            deviceDao.saveAndFlush(device);

            // 更新结束使用时间
            BorrowHistory borrowHistory = borrowHistoryDao.findByDeviceId(deviceId);
            borrowHistory.setEndTime(new Date());

            borrowHistoryDao.saveAndFlush(borrowHistory);
        });

        return "归还成功, 请杀毒";
    }

}
