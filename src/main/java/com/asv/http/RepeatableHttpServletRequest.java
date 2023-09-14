package com.asv.http;

import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


import java.io.*;

public class RepeatableHttpServletRequest extends HttpServletRequestWrapper {
    private final byte[] body;

    public RepeatableHttpServletRequest(HttpServletRequest request)  throws IOException {
        super(request);
        body = StreamUtils.copyToByteArray(request.getInputStream());
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            ByteArrayInputStream bis = new ByteArrayInputStream(body);

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };
    }
}