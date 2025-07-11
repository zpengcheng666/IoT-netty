package com.sydh.common.utils.ip;

import com.sydh.common.utils.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;


public class IpUtils {
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String str = request.getHeader("x-forwarded-for");
        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            str = request.getHeader("Proxy-Client-IP");
        }
        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            str = request.getHeader("X-Forwarded-For");
        }
        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            str = request.getHeader("WL-Proxy-Client-IP");
        }
        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            str = request.getHeader("X-Real-IP");
        }

        if (str == null || str.length() == 0 || "unknown".equalsIgnoreCase(str)) {
            str = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(str) ? "127.0.0.1" : getMultistageReverseProxyIp(str);
    }


    public static boolean internalIp(String ip) {
        byte[] arrayOfByte = textToNumericFormatV4(ip);
        return (c(arrayOfByte) || "127.0.0.1".equals(ip));
    }


    private static boolean c(byte[] paramArrayOfbyte) {
        if (StringUtils.isNull(paramArrayOfbyte) || paramArrayOfbyte.length < 2) {
            return true;
        }
        byte b1 = paramArrayOfbyte[0];
        byte b2 = paramArrayOfbyte[1];

        byte b3 = 10;

        byte b4 = -84;
        byte b5 = 16;
        byte b6 = 31;

        byte b7 = -64;
        byte b8 = -88;
        switch (b1) {

            case 10:
                return true;
            case -84:
                if (b2 >= 16 && b2 <= 31) {
                    return true;
                }
            case -64:
                switch (b2) {

                    case -88:
                        return true;
                }
                break;
        }
        return false;
    }


    public static byte[] textToNumericFormatV4(String text) {
        if (text.length() == 0) {
            return null;
        }

        byte[] arrayOfByte = new byte[4];
        String[] arrayOfString = text.split("\\.", -1);

        try {
            long l;
            byte b;
            switch (arrayOfString.length) {

                case 1:
                    l = Long.parseLong(arrayOfString[0]);
                    if (l < 0L || l > 4294967295L) {
                        return null;
                    }
                    arrayOfByte[0] = (byte) (int) (l >> 24L & 0xFFL);
                    arrayOfByte[1] = (byte) (int) ((l & 0xFFFFFFL) >> 16L & 0xFFL);
                    arrayOfByte[2] = (byte) (int) ((l & 0xFFFFL) >> 8L & 0xFFL);
                    arrayOfByte[3] = (byte) (int) (l & 0xFFL);


                    return arrayOfByte;
                case 2:
                    l = Integer.parseInt(arrayOfString[0]);
                    if (l < 0L || l > 255L) return null;
                    arrayOfByte[0] = (byte) (int) (l & 0xFFL);
                    l = Integer.parseInt(arrayOfString[1]);
                    if (l < 0L || l > 16777215L) return null;
                    arrayOfByte[1] = (byte) (int) (l >> 16L & 0xFFL);
                    arrayOfByte[2] = (byte) (int) ((l & 0xFFFFL) >> 8L & 0xFFL);
                    arrayOfByte[3] = (byte) (int) (l & 0xFFL);
                    return arrayOfByte;
                case 3:
                    for (b = 0; b < 2; b++) {
                        l = Integer.parseInt(arrayOfString[b]);
                        if (l < 0L || l > 255L) return null;
                        arrayOfByte[b] = (byte) (int) (l & 0xFFL);
                    }
                    l = Integer.parseInt(arrayOfString[2]);
                    if (l < 0L || l > 65535L) return null;
                    arrayOfByte[2] = (byte) (int) (l >> 8L & 0xFFL);
                    arrayOfByte[3] = (byte) (int) (l & 0xFFL);
                    return arrayOfByte;
                case 4:
                    for (b = 0; b < 4; b++) {
                        l = Integer.parseInt(arrayOfString[b]);
                        if (l < 0L || l > 255L) return null;
                        arrayOfByte[b] = (byte) (int) (l & 0xFFL);
                    }
                    return arrayOfByte;
            }
            return null;
        } catch (NumberFormatException numberFormatException) {
            return null;
        }
    }


    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException unknownHostException) {


            return "127.0.0.1";
        }
    }


    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException unknownHostException) {


            return "未知";
        }
    }


    public static String getMultistageReverseProxyIp(String ip) {
        if (ip != null && ip.indexOf(",") > 0) {

            String[] arrayOfString = ip.trim().split(",");
            for (String str : arrayOfString) {

                if (false == isUnknown(str)) {

                    ip = str;
                    break;
                }
            }
        }
        return ip;
    }


    public static boolean isUnknown(String checkString) {
        return (StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString));
    }
}
