package com.sydh.common.utils.sign;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Md5Utils {
    private static final Logger cJ = LoggerFactory.getLogger(Md5Utils.class);


    private static byte[] p(String paramString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(paramString.getBytes("UTF-8"));
            return messageDigest.digest();

        } catch (Exception exception) {

            cJ.error("MD5 Error...", exception);

            return null;
        }
    }

    private static final String d(byte[] paramArrayOfbyte) {
        if (paramArrayOfbyte == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer(paramArrayOfbyte.length * 2);


        for (byte b = 0; b < paramArrayOfbyte.length; b++) {

            if ((paramArrayOfbyte[b] & 0xFF) < 16) {
                stringBuffer.append("0");
            }
            stringBuffer.append(Long.toString((paramArrayOfbyte[b] & 0xFF), 16));
        }
        return stringBuffer.toString();
    }


    public static String hash(String s) {
        try {
            return new String(d(p(s)).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } catch (Exception exception) {

            cJ.error("not supported charset...{}", exception);
            return s;
        }
    }
}
