package com.asv.controller.admin;

import com.asv.constant.Constant;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = "/win32")
public class ReleaseController {

    @GetMapping("/{filename:.+}")
    public void downloadFile(@PathVariable String filename, HttpServletResponse httpServletResponse) throws IOException {
        File fileDirectory = new File(Constant.RELEASE_PATH);
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {//目录创建失败
                throw new RuntimeException("创建目录失败(" + Constant.RELEASE_PATH + ")");
            }
        }

        Path filePath = Paths.get(Constant.RELEASE_PATH, filename);
        if (!filePath.toFile().exists()) {
            throw new RuntimeException("请求文件不存在(" + filePath.toFile().getName() + ")");
        }
        Resource resource = new UrlResource(filePath.toUri());

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + filename);
        httpServletResponse.setContentType(mediaType.toString());

        // 读取文件内容并写入响应输出流
        InputStream inputStream = resource.getInputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            httpServletResponse.getOutputStream().write(buffer, 0, bytesRead);
        }
        httpServletResponse.flushBuffer();
    }

}
