package com.asv.controller.main;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.asv.aspect.DeviceUpdateAnnotation;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.*;
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
        String deviceId = json.getString("deviceId");

        // 当virus=0时可传入virusDate=null, virus=1传入virusDate HH:MM:SS
        Integer virus = json.getInteger("virus");
        Date virusDate = json.getDate("virusDate");

        // 存在光盘其他设备的情况可能无实际路径
        if (pnDevice != null) {
            Device device = deviceDao.findByPnDevice(pnDevice);
            if (device != null) {
                throw new RuntimeException("添加失败(存在设备实际路径)");
            }
        }

        if (deviceId != null) {
            Device device = deviceDao.findByDeviceId(deviceId);
            if (device != null) {
                throw new RuntimeException("添加失败(存在重复设备ID)");
            }
        }

        String type = json.getString("type");
        User u = (User) httpServletRequest.getAttribute("login");

        Device device1 = Device.builder().name(json.getString("name"))
                .status(DeviceStatus.IN_STORE.getValue())
                .virus(virus)
                .virusDate(virusDate)
                .type(type)
                .capacity(json.getString("capacity"))
                .program(json.getString("program"))
                .departId(json.getInteger("departId"))
                .sectionId(json.getInteger("sectionId"))
                .pnDevice(pnDevice)
                .deviceId(deviceId)
                .remark(json.getString("remark"))
                .storeDate(new Date())
                .updateDate(new Date())
                .storeUserId(u.getUserId())
                .build();

        deviceDao.save(device1);
        return "添加成功:" + deviceId;
    }

    @RequestMapping(value = "/addMulti", method = RequestMethod.POST)
    public String addMulti(HttpServletRequest httpServletRequest, @RequestBody JSONObject json) {
        JSONArray deviceInfosArray = json.getJSONArray("deviceInfos");

        // 当virus=0时可传入virusDate=null, virus=1传入virusDate HH:MM:SS
        Integer virus = json.getInteger("virus");
        Date virusDate = json.getDate("virusDate");

        for (int i = 0; i < deviceInfosArray.size(); i++) {
            JSONObject deviceInfoObject = deviceInfosArray.getJSONObject(i);
            Device device = deviceDao.findByPnDevice(deviceInfoObject.getString("pnDevice"));
            if (device != null) {
                throw new RuntimeException("添加失败(存在设备实际路径) - " + device.getDeviceId());
            }
        }

        User u = (User) httpServletRequest.getAttribute("login");

        for (int i = 0; i < deviceInfosArray.size(); i++) {
            JSONObject deviceInfoObject = deviceInfosArray.getJSONObject(i);

            String type = deviceInfoObject.getString("type");
            String prefix = "FM-";

            // 查找总数
            Long maxId = deviceDao.findMaxId();
            String deviceId = prefix + String.format("%04d", maxId == null ? 1 : maxId + 1);

            Device device1 = Device.builder().name(deviceInfoObject.getString("name"))
                    .capacity(deviceInfoObject.getString("capacity"))
                    .pnDevice(deviceInfoObject.getString("pnDevice"))
                    .type(type)
                    .status(DeviceStatus.IN_STORE.getValue())
                    .virus(virus)
                    .virusDate(virusDate)
                    .program(json.getString("program"))
                    .departId(json.getInteger("departId"))
                    .sectionId(json.getInteger("sectionId"))
                    .deviceId(deviceId)
                    .remark(json.getString("remark"))
                    .storeDate(new Date())
                    .updateDate(new Date())
                    .storeUserId(u.getUserId())
                    .build();

            deviceDao.save(device1);
        }

        return "";
    }

    @DeviceUpdateAnnotation
    @RequestMapping(value = "/checkUsingDevice", method = RequestMethod.POST)
    public JSONObject checkUsingDevice(HttpServletRequest httpServletRequest) {
        String deviceId = httpServletRequest.getParameter("deviceId");

        Device device = deviceDao.findByStatusAndDeviceId(DeviceStatus.USING.getValue(), deviceId);

        if (device == null) {
            throw new RuntimeException("不存在该设备ID或状态不为使用中 - " + deviceId);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", device.getDeviceId());
        jsonObject.put("deviceName", device.getName());
        jsonObject.put("devicePath", device.getPnDevice());

        return jsonObject;
    }

    @DeviceUpdateAnnotation
    @RequestMapping(value = "/checkInDevice", method = RequestMethod.POST)
    public JSONObject checkInDevice(HttpServletRequest httpServletRequest) {
        String deviceId = httpServletRequest.getParameter("deviceId");

        Device device = deviceDao.findByStatusAndDeviceId(DeviceStatus.IN_STORE.getValue(), deviceId);

        if (device == null) {
            throw new RuntimeException("不存在该设备ID或状态不为在库 - " + deviceId);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", device.getDeviceId());
        jsonObject.put("deviceName", device.getName());
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
                                            @RequestParam(value = "usingLine", required = false) String usingLine,
                                            @RequestParam(value = "sort", required = false) String sort,
                                            @RequestParam("pageNum") int pageNum,
                                            @RequestParam("pageSize") int pageSize) {

        Sort sort1 = ObjectUtils.isEmpty(sort) ? Sort.by(Sort.Direction.DESC, "storeDate") :
                Sort.by(Sort.Order.desc("updateDate"), Sort.Order.desc("deviceId"));

        Pageable pageable = PageRequest.of(pageNum, pageSize, sort1);
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
        if (ObjectUtils.isEmpty(usingLine)) {
            usingLine = null;
        }
        if (ObjectUtils.isEmpty(remark)) {
            remark = null;
        }

        Page<DeviceModel> page = deviceDao.searchDevicesByKeyword(deviceId, deviceName, status, capacity, type,
                program, departId, sectionId, virus, currentUserName, usingLine, remark, pageable);
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

    @DeviceUpdateAnnotation
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
        device1.setUpdateDate(new Date());

        deviceDao.saveAndFlush(device1);
        return "更新设备成功";
    }

    @DeviceUpdateAnnotation
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

    @DeviceUpdateAnnotation
    @PostMapping(value = "/getBorrowDetail")
    public List<BorrowHistory> getBorrowDetail(HttpServletRequest httpServletRequest) {
        Device device = deviceDao.findByDeviceId(httpServletRequest.getParameter("deviceId"));

        if (null == device) {
            throw new RuntimeException("没有该设备");
        }
        List<BorrowHistory> borrowHistoryList = borrowHistoryDao.findByDeviceId(device.getDeviceId(),
                Sort.by(Sort.Direction.DESC, "id"));

        // 为了避免看到用户id
        return borrowHistoryList.stream()
                .peek(bh -> bh.setUserId(null))
                .collect(Collectors.toList());
    }

    @DeviceUpdateAnnotation
    @PostMapping(value = "/getVirusDetail")
    public List<VirusHistory> getVirusDetail(HttpServletRequest httpServletRequest) {
        Device device = deviceDao.findByDeviceId(httpServletRequest.getParameter("deviceId"));

        if (null == device) {
            throw new RuntimeException("没有该设备");
        }
        List<VirusHistory> virusHistoryList = virusHistoryDao
                .findByDeviceId(device.getDeviceId(), Sort.by(Sort.Direction.DESC, "id"));

        // 为了避免看到用户id
        return virusHistoryList.stream()
                .peek(bh -> bh.setUserId(null))
                .collect(Collectors.toList());
    }

    @DeviceUpdateAnnotation
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
        List<VirusHistory> virusHistoryList = virusHistoryDao.findByDeviceIdIn(deviceIdList);
        List<BorrowHistory> borrowHistoryList = borrowHistoryDao.findByDeviceIdIn(deviceIdList);
        List<LogRecord> logRecordList = logRecordDao.findByDeviceIdIn(deviceIdList);

        writeToCsv(response, deviceModels, virusHistoryList, borrowHistoryList, logRecordList);
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
                           @RequestParam(value = "remark", required = false) String remark,
                           @RequestParam(value = "usingLine", required = false) String usingLine, HttpServletResponse response) throws IOException {

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
        if (ObjectUtils.isEmpty(usingLine)) {
            usingLine = null;
        }
        if (ObjectUtils.isEmpty(remark)) {
            remark = null;
        }

        List<DeviceModel> deviceModelList = deviceDao.exportByForm(deviceId, deviceName, status, capacity, type, program, departId, sectionId, virus, currentUserName, usingLine, remark);

        List<String> deviceIdList = deviceModelList.stream()
                .map(DeviceModel::getDeviceId)
                .collect(Collectors.toList());

        List<VirusHistory> virusHistoryList = virusHistoryDao.findByDeviceIdIn(deviceIdList);
        List<BorrowHistory> borrowHistoryList = borrowHistoryDao.findByDeviceIdIn(deviceIdList);
        List<LogRecord> logRecordList = logRecordDao.findByDeviceIdIn(deviceIdList);

        writeToCsv(response, deviceModelList, virusHistoryList, borrowHistoryList, logRecordList);
    }


    private void writeToCsv(HttpServletResponse response, List<DeviceModel> deviceModelList, List<VirusHistory> virusHistoryList,
                            List<BorrowHistory> borrowHistoryList, List<LogRecord> logRecordList) throws IOException {
        deviceModelList.forEach(deviceModel -> {
                if (deviceModel.get_status() == null || deviceModel.get_virus() == null) {
                    throw new RuntimeException("枚举类型空指针错误 !");
                }
                deviceModel.setStatus(DeviceStatus.getDescriptionByValue(deviceModel.get_status()));
                deviceModel.setVirus(AntivirusStatus.getDescriptionByValue(deviceModel.get_virus()));
        });

//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment; filename=" + "deviceInfo_" +
                System.currentTimeMillis() + ".xlsx");

        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "详细信息").head(DeviceModel.class).build();
            excelWriter.write(deviceModelList, writeSheet);

            WriteSheet writeSheet2 = EasyExcel.writerSheet(1, "杀毒记录").head(VirusHistory.class).build();
            excelWriter.write(virusHistoryList, writeSheet2);

            WriteSheet writeSheet3 = EasyExcel.writerSheet(2, "借出记录").head(BorrowHistory.class).build();
            excelWriter.write(borrowHistoryList, writeSheet3);

            WriteSheet writeSheet4 = EasyExcel.writerSheet(3, "操作记录").head(LogRecord.class).build();
            excelWriter.write(logRecordList, writeSheet4);
        }

    }

    @PostMapping(value = "/scanReturn")
    public String scanReturn(HttpServletRequest httpServletRequest, @RequestBody List<String> deviceIdList) {
        String confirm = httpServletRequest.getParameter("confirm");

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

            if (confirm.equalsIgnoreCase("N")) {
                device.setVirus(AntivirusStatus.SCANNING.getValue());
            } else {
                device.setVirus(AntivirusStatus.SCANNED.getValue());
            }

            device.setUsingPost(null);
            device.setUsingLine(null);
            device.setCurrentUserId(null);
            device.setCurrentUserName(null);
            device.setBorrowDate(null);
            device.setUpdateDate(new Date());

            deviceDao.saveAndFlush(device);

            // 更新结束使用时间
            BorrowHistory borrowHistory = borrowHistoryDao.findHistoryByDeviceId(deviceId);
            // 这里加个判断是为了中途导入的数据情况会出现不整合
            if (borrowHistory != null) {
                borrowHistory.setEndTime(new Date());
                borrowHistoryDao.saveAndFlush(borrowHistory);
            }

        });

        return "归还成功, 请查看杀毒状态";
    }

    @GetMapping(value = "/download")
    private void downloadTemplate(HttpServletResponse response) throws IOException {
        // 获取文件路径
        String filePath = "download/template.xlsx";

        // 加载文件资源
        ClassPathResource resource = new ClassPathResource(filePath);

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + resource.getFilename());

        // 将文件内容输出到response中
        InputStream inputStream = resource.getInputStream();
        OutputStream outputStream = response.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();
    }

    @PostMapping(value = "/importExcel")
    public String importExcel(HttpServletRequest httpServletRequest, @RequestPart("file") MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        ExcelReaderBuilder readerBuilder = EasyExcel.read(file.getInputStream());

        if ("xls".equalsIgnoreCase(extension)) {
            // 处理Excel 97-2003格式（.xls）
            readerBuilder = readerBuilder.excelType(ExcelTypeEnum.XLS);
        } else {
            // 处理Excel 2007及以上格式（.xlsx）
            readerBuilder = readerBuilder.excelType(ExcelTypeEnum.XLSX);
        }

        List<DeviceModel> list = readerBuilder.head(DeviceModel.class)
                .sheet()
                .doReadSync();

        // device excel import
        User u = (User) httpServletRequest.getAttribute("login");

        int lineNo = 2;
        for (DeviceModel deviceModel : list) {
            if (deviceModel.getDeviceId() == null) {
                throw new RuntimeException("编号为空 - 第" + lineNo + "行");
            }
            if (deviceModel.getDeviceName() == null) {
                throw new RuntimeException("设备名称为空 - 第" + lineNo + "行");
            }
            if (deviceModel.getVirusDate() == null) {
                throw new RuntimeException("杀毒时间为空 - 第" + lineNo + "行");
            }

            Device device = deviceDao.findByDeviceId(deviceModel.getDeviceId());

            if (device != null) {
                throw new RuntimeException("已存在重复设备ID - " + deviceModel.getDeviceId());
            }

            // 判断状态
            int returnStatus = DeviceStatus.getValueByDesc(deviceModel.getStatus());
            if (-1 == returnStatus) {
                throw new RuntimeException("设备状态输入有误 - " + deviceModel.getDeviceId());
            }

            if (1 == returnStatus) {
                if (deviceModel.getCurrentUserName() == null) {
                    throw new RuntimeException("使用中的领用人不为空 - " + deviceModel.getDeviceId());
                }
            }

            lineNo++;
        }

        Set<String> duplicateDeviceIds = new HashSet<>();
        boolean hasDuplicate = list.stream()
                .map(DeviceModel::getDeviceId)
                .distinct()
                .anyMatch(deviceId -> {
                    boolean isDuplicate = list.stream()
                            .filter(deviceModel -> deviceModel.getDeviceId().equals(deviceId))
                            .count() > 1;
                    if (isDuplicate) {
                        duplicateDeviceIds.add(deviceId);
                    }
                    return isDuplicate;
                });

        if (hasDuplicate) {
            throw new RuntimeException("EXCEL中存在重复设备ID - " + duplicateDeviceIds);
        }

        for (DeviceModel deviceModel : list) {
            Device device = Device.builder()
                    .deviceId(deviceModel.getDeviceId())
                    .name(deviceModel.getDeviceName())
                    .program(deviceModel.getProgram())
                    .status(DeviceStatus.getValueByDesc(deviceModel.getStatus()))
                    .virusDate(deviceModel.getVirusDate())
                    .usingLine(deviceModel.getUsingLine())
                    .usingPost(deviceModel.getUsingPost())
                    .currentUserName(deviceModel.getCurrentUserName())
                    .borrowDate(deviceModel.getBorrowDate())
                    .scrapDate(deviceModel.getScrapDate())
                    .remark(deviceModel.getRemark())
                    .virus(AntivirusStatus.SCANNED.getValue())
                    .storeDate(new Date())
                    .storeUserId(u.getUserId())
                    .updateDate(new Date())
                    .build();

            deviceDao.save(device);
        }
        return "导入成功";
    }

    @PostMapping(value = "/bindDevice")
    public String bindDevice(HttpServletRequest httpServletRequest) {
        String deviceId = httpServletRequest.getParameter("deviceId");
        String capacity = httpServletRequest.getParameter("capacity");
        String type = httpServletRequest.getParameter("type");
        String pnDevice = httpServletRequest.getParameter("pnDevice");

        Device device = deviceDao.findByDeviceId(deviceId);

        if (device == null) {
            throw new RuntimeException("不存在该设备ID - " + deviceId);
        }

        Device device1 = deviceDao.findByPnDevice(pnDevice);
        if (device1 != null) {
            throw new RuntimeException("重复实际路径 - " + device1.getDeviceId());
        }

        device.setPnDevice(pnDevice);
        device.setCapacity(capacity);
        device.setType(type);
        device.setUpdateDate(new Date());

        deviceDao.saveAndFlush(device);

        return "绑定成功";

    }

}
