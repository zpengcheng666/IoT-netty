package com.sydh.common.filter;

import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.html.EscapeUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public String[] getParameterValues(String name) {
        String[] var2 = super.getParameterValues(name);
        if (var2 == null) {
            return super.getParameterValues(name);
        } else {
            int var3 = var2.length;
            String[] var4 = new String[var3];

            for(int var5 = 0; var5 < var3; ++var5) {
                var4[var5] = EscapeUtil.clean(var2[var5]).trim();
            }

            return var4;
        }
    }

    public ServletInputStream getInputStream() throws IOException {
        if (!this.isJsonRequest()) {
            return super.getInputStream();
        } else {
            String var1 = IOUtils.toString(super.getInputStream(), "utf-8");
            if (StringUtils.isEmpty(var1)) {
                return super.getInputStream();
            } else {
                var1 = EscapeUtil.clean(var1).trim();
                final byte[] var2 = var1.getBytes("utf-8");
                final ByteArrayInputStream var3 = new ByteArrayInputStream(var2);
                return new ServletInputStream() {
                    public boolean isFinished() {
                        return true;
                    }

                    public boolean isReady() {
                        return true;
                    }

                    public int available() throws IOException {
                        return var2.length;
                    }

                    public void setReadListener(ReadListener readListener) {
                    }

                    public int read() throws IOException {
                        return var3.read();
                    }
                };
            }
        }
    }

    public boolean isJsonRequest() {
        String var1 = super.getHeader("Content-Type");
        return StringUtils.startsWithIgnoreCase(var1, "application/json");
    }
}