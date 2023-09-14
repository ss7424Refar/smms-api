package com.asv.controller.main;

import com.asv.constant.AntivirusStatus;
import com.asv.constant.Constant;
import com.asv.dao.DeviceDao;
import com.asv.dao.VirusHistoryDao;
import com.asv.entity.Device;
import com.asv.entity.User;
import com.asv.entity.VirusHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/virus")
public class VirusController {
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    VirusHistoryDao virusHistoryDao;

    // 自动杀毒
    @RequestMapping(value = "/autoUpload", method = RequestMethod.POST)
    public String autoVirus(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) {
        String deviceId = httpServletRequest.getParameter("deviceId");
        User u = (User) httpServletRequest.getAttribute("login");
        Device device = deviceDao.findByDeviceId(deviceId);

        if (ObjectUtils.isEmpty(device)) {
            throw new RuntimeException("没有该设备 - " + deviceId);
        }

        //获取上传文件的名字
        String fileName = file.getOriginalFilename();
        //获取上传文件的后缀名
        assert fileName != null;
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //生成文件名称UUID
        UUID uuid = UUID.randomUUID();
        //生成新的文件名称
        String fileNewName = uuid + suffixName;
        //存放目录
        File fileDirectory = new File(Constant.VIRUS_LOG_PATH);
        //存放路径
        File destFile = new File(Constant.VIRUS_LOG_PATH + fileNewName);
        //判断目录是否存在
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {//目录创建失败
                throw new RuntimeException("创建目录失败(" + deviceId + ")");
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!destFile.exists()) {
            throw new RuntimeException(destFile.getName() + " 读取文件失败(" + deviceId + ")");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(destFile))) {
            String line;
            String lastLine = "";

            while ((line = br.readLine()) != null) {
                lastLine = line;
            }

            if (lastLine.startsWith("Return Code")) {
                String[] code = lastLine.trim().split(":");
                int returnCode = Integer.parseInt(code[1].trim());
                String codeString = "";

                if (returnCode == 0) {
                    codeString = "未发现威胁";
                    device.setVirus(AntivirusStatus.SCANNED.getValue());
                } else if (returnCode == 1) {
                    codeString = "发现威胁并已清除";
                    device.setVirus(AntivirusStatus.SCANNED.getValue());
                } else if (returnCode == 10) {
                    codeString = "无法扫描某些文件（可能是威胁)";
                    device.setVirus(AntivirusStatus.SCANNED.getValue());
                    // 50以后的数字需要前端标成 类似警告
                } else if (returnCode == 50) {
                    codeString = "发现威胁";
                    device.setVirus(AntivirusStatus.WARNING_SCANNED.getValue());
                } else if (returnCode == 100) {
                    codeString = "错误";
                    device.setVirus(AntivirusStatus.WARNING_SCANNED.getValue());
                } else if (returnCode > 100) {
                    codeString = "未扫描文件，该文件可能被感染";
                    device.setVirus(AntivirusStatus.WARNING_SCANNED.getValue());
                } else {
                    codeString = "N/A(" + returnCode + ")";
                    device.setVirus(AntivirusStatus.WARNING_SCANNED.getValue());
                }

                device.setVirusDate(new Date());

                VirusHistory virusHistory = new VirusHistory();

                virusHistory.setDeviceId(deviceId);
                virusHistory.setUploadDate(new Date());
                virusHistory.setUserId(u.getUserId());
                virusHistory.setUserName(u.getUserName());
                virusHistory.setReturnCode(String.valueOf(returnCode));
                virusHistory.setCodeString(codeString);
                virusHistory.setLogPath(fileNewName);
                virusHistory.setRemark("自动杀毒");

                virusHistoryDao.save(virusHistory);

                return "上传杀毒日志成功";

            } else {
                throw new RuntimeException(destFile.getName() + " 读取ReturnCode失败(" + deviceId + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("上传杀毒日志失败");
    }

    // 手动杀毒
    @RequestMapping(value = "/manual", method = RequestMethod.POST)
    public String manualVirus(HttpServletRequest httpServletRequest, @RequestBody List<String> deviceIdList) {
        User u = (User) httpServletRequest.getAttribute("login");

        // 杀毒结果等额外的信息
        String extString = httpServletRequest.getParameter("extString");

        deviceIdList.forEach(deviceId -> {
            Device device = deviceDao.findByDeviceId(deviceId);
            if (device == null) {
                throw new RuntimeException("找不到该设备ID - " + deviceId);
            }
        });

        deviceIdList.forEach(deviceId -> {
            Device device = deviceDao.findByDeviceId(deviceId);

            device.setVirus(AntivirusStatus.SCANNED.getValue());
            device.setVirusDate(new Date());

            deviceDao.saveAndFlush(device);

            VirusHistory virusHistory = new VirusHistory();

            virusHistory.setDeviceId(deviceId);
            virusHistory.setUploadDate(new Date());
            virusHistory.setUserId(u.getUserId());
            virusHistory.setUserName(u.getUserName());
            virusHistory.setReturnCode("-");
            virusHistory.setCodeString(extString);
            virusHistory.setLogPath("");
            virusHistory.setRemark("手动杀毒");

            virusHistoryDao.save(virusHistory);

        });

        return "杀毒记录成功";
    }

    // 查看日志文件
    @RequestMapping(value = "/showLog", method = RequestMethod.POST)
    public String showLog(HttpServletRequest httpServletRequest) throws IOException {
        String fileName = httpServletRequest.getParameter("fileName");
        List<String> lines = Files.readAllLines(Paths.get(Constant.VIRUS_LOG_PATH + "/" + fileName),
                StandardCharsets.UTF_8);
        StringBuffer stringBuffer = new StringBuffer();
        lines.forEach(line -> {
            stringBuffer.append(line).append("\n");
        });

        return stringBuffer.toString();

    }
}
