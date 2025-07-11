package com.sydh.common.utils.wechat;

import java.security.MessageDigest;
import java.util.Arrays;


public class SHA1 {
    public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws AesException {
        try {
            String[] arrayOfString = {token, timestamp, nonce, encrypt};
            StringBuffer stringBuffer1 = new StringBuffer();

            Arrays.sort((Object[]) arrayOfString);
            for (byte b1 = 0; b1 < 4; b1++) {
                stringBuffer1.append(arrayOfString[b1]);
            }
            String str1 = stringBuffer1.toString();

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(str1.getBytes());
            byte[] arrayOfByte = messageDigest.digest();

            StringBuffer stringBuffer2 = new StringBuffer();
            String str2 = "";
            for (byte b2 = 0; b2 < arrayOfByte.length; b2++) {
                str2 = Integer.toHexString(arrayOfByte[b2] & 0xFF);
                if (str2.length() < 2) {
                    stringBuffer2.append(0);
                }
                stringBuffer2.append(str2);
            }
            return stringBuffer2.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new AesException(-40003);
        }
    }
}
