package com.sydh.common.filter;

import com.sydh.common.utils.http.HttpHelper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RepeatedlyRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] ad;

    public RepeatedlyRequestWrapper(HttpServletRequest request, ServletResponse response) throws IOException {
        super(request);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        this.ad = HttpHelper.getBodyString(request).getBytes("UTF-8");
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream var1 = new ByteArrayInputStream(this.ad);
        return new ServletInputStream() {
            public int read() throws IOException {
                return var1.read();
            }

            public int available() throws IOException {
                return RepeatedlyRequestWrapper.this.ad.length;
            }

            public boolean isFinished() {
                return false;
            }

            public boolean isReady() {
                return false;
            }

            public void setReadListener(ReadListener readListener) {
            }
        };
    }
}