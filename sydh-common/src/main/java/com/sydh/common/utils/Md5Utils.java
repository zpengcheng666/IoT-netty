package com.sydh.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


public class Md5Utils {
    private static final String aF = "MD5";
    private static final String aG = "UTF-8";

    public static String md5(String input) {
        return md5(input, 1);
    }

    public static String md5(String input, int iterations) {
        try {
            return EncodeUtils.encodeHex(DigestUtils.digest(input.getBytes("UTF-8"), "MD5", (byte[]) null, iterations));
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            return "";
        }
    }

    public static byte[] md5(byte[] input) {
        return md5(input, 1);
    }

    public static byte[] md5(byte[] input, int iterations) {
        return DigestUtils.digest(input, "MD5", (byte[]) null, iterations);
    }

    public static byte[] md5(InputStream input) throws IOException {
        return DigestUtils.digest(input, "MD5");
    }

    public static boolean isMd5(String str) {
        byte b1 = 0;
        for (byte b2 = 0; b2 < str.length(); b2++) {
            switch (str.charAt(b2)) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                    b1++;
                    if (32 <= b1) {
                        return true;
                    }
                    break;
                case '/':
                    if (b2 + 10 < str.length()) {
                        char c1 = str.charAt(b2 + 1);
                        char c2 = str.charAt(b2 + 8);
                        if ('/' == c2 && ('s' == c1 || 'S' == c1)) {
                            return true;
                        }
                    }
                default:
                    b1 = 0;
                    break;
            }
        }
        return false;
    }
}
