package com.sydh.common.utils.sign;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class SignUtils {
    public static boolean checkSignature(String token, String signature, String timestamp, String nonce) {
        String[] arrayOfString = {token, timestamp, nonce};
        Arrays.sort((Object[]) arrayOfString);


        StringBuilder stringBuilder = new StringBuilder();
        for (byte b = 0; b < arrayOfString.length; b++) {
            stringBuilder.append(arrayOfString[b]);
        }
        MessageDigest messageDigest = null;
        String str = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");

            byte[] arrayOfByte = messageDigest.digest(stringBuilder.toString().getBytes());
            str = e(arrayOfByte);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }

        stringBuilder = null;

        return (str != null) ? str.equals(signature.toUpperCase()) : false;
    }


    private static String e(byte[] paramArrayOfbyte) {
        String str = "";
        for (byte b = 0; b < paramArrayOfbyte.length; b++) {
            str = str + a(paramArrayOfbyte[b]);
        }
        return str;
    }


    private static String a(byte paramByte) {
        char[] arrayOfChar1 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] arrayOfChar2 = new char[2];
        arrayOfChar2[0] = arrayOfChar1[paramByte >>> 4 & 0xF];
        arrayOfChar2[1] = arrayOfChar1[paramByte & 0xF];
        return new String(arrayOfChar2);
    }
}
